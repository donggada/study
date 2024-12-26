package com.side.mvcTraffic.domain.waitUser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserQueueService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final String USER_QUEUE_WAIT_KEY = "users:queue:%s:wait";
    private final String USER_QUEUE_WAIT_KEY_FOR_SCAN = "users:queue:*:wait";
    private final String USER_QUEUE_PROCEED_KEY = "users:queue:%s:proceed";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${scheduler.enabled}")
    private Boolean scheduling = false;

    @Transactional
    public Long registerWaitQueue(String queue, Long userId) {
        Long unixTimestamp = Instant.now().getEpochSecond();

        if (redisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY.formatted(queue), userId.toString()) == null) {
            redisTemplate.opsForZSet().add(USER_QUEUE_WAIT_KEY.formatted(queue), userId.toString(), unixTimestamp);
        }

        return getRank(queue, userId);
    }

    public Long allowUser(final String queue, final Long count) {
        return redisTemplate.opsForZSet().popMin(USER_QUEUE_WAIT_KEY.formatted(queue), count)
                .stream()
                .map(member -> redisTemplate.opsForZSet().add(USER_QUEUE_PROCEED_KEY.formatted(queue), member.getValue(), Instant.now().getEpochSecond()))
                .toList().stream().count();
    }

    public Boolean isAllowed(final String queue, final Long userId) {
        Long rank = redisTemplate.opsForZSet().rank(USER_QUEUE_PROCEED_KEY.formatted(queue), userId.toString());

        if (rank == null) {
            return false;
        }

        return rank > -1;
    }

    public Long getRank(final String queue, final Long userId) {
        Long rank = redisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY.formatted(queue), userId.toString());

        if (rank == null) {
            return -1L;
        }

        return rank > -1 ? rank + 1 : rank;
    }

    public Boolean isAllowedByToken(final String queue, final Long userId, final String token) {
        return generateToken(queue, userId).equalsIgnoreCase(token);
    }


    public String generateToken(final String queue, final Long userId) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            var input = "user-queue-%s-%d".formatted(queue, userId);
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte aByte: encodedHash) {
                hexString.append(String.format("%02x", aByte));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


//    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
    public void scheduleAllowUser() {
        if (!scheduling) {
            log.info("passed scheduling...");
            return;
        }
        log.info("called scheduling...");

        var maxAllowUserCount = 100L;
        redisTemplate.scan(ScanOptions.scanOptions()
                        .match(USER_QUEUE_WAIT_KEY_FOR_SCAN)
                        .count(100)
                        .build())
                .stream()
                .map(key -> key.split(":")[2]).map(queue -> allowUser(queue, maxAllowUserCount))
                .forEach(i -> {
                    log.info("Tried %d and allowed %d members of queue".formatted(maxAllowUserCount, i));
                });
    }
}
