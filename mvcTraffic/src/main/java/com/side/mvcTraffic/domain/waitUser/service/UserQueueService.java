package com.side.mvcTraffic.domain.waitUser.service;

import com.side.mvcTraffic.domain.waitUser.repository.WaitUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;

import static com.side.mvcTraffic.global.exception.ErrorCode.QUEUE_ALREADY_REGISTERED_USER;


@Service
@RequiredArgsConstructor
public class UserQueueService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final String USER_QUEUE_WAIT_KEY = "users:queue:%s:wait";
    private final String USER_QUEUE_WAIT_KEY_FOR_SCAN = "users:queue:*:wait";
    private final String USER_QUEUE_PROCEED_KEY = "users:queue:%s:proceed";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${scheduler.enabled}")
    private Boolean scheduling = false;

    public Optional<Long> registerWaitQueue(String queue, Long userId) {
        Long unixTimestamp = Instant.now().getEpochSecond();

        Boolean addCheck = redisTemplate.opsForZSet().add(USER_QUEUE_WAIT_KEY.formatted(queue), userId.toString(), unixTimestamp);

        if (!addCheck) {
            return Optional.of(-1L);
        }

        Long rank = redisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY.formatted(queue), userId.toString());

        if (rank < -1) {
            return Optional.of(rank);
        }

        return Optional.of(rank + 1);
    }

    public Mono<Long> allowUser(final String queue, final Long count) {
        return reactiveRedisTemplate.opsForZSet().popMin(USER_QUEUE_WAIT_KEY.formatted(queue), count)
                .flatMap(member -> reactiveRedisTemplate.opsForZSet().add(USER_QUEUE_PROCEED_KEY.formatted(queue), member.getValue(), Instant.now().getEpochSecond()))
                .count();
    }

    public Mono<Boolean> isAllowed(final String queue, final Long userId) {
        return reactiveRedisTemplate.opsForZSet().rank(USER_QUEUE_PROCEED_KEY.formatted(queue), userId.toString())
                .defaultIfEmpty(-1L)
                .map(rank -> rank > -1 );
    }

    public Long getRank(final String queue, final Long userId) {
        Long rank = redisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY.formatted(queue), userId.toString());

        if (rank < -1) {
            return  -1L;
        }

        return rank + 1;
    }





}
