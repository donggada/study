package com.side.mvcTraffic.domain.waitUser.controller;


import com.side.mvcTraffic.domain.waitUser.dto.AllowUserResponse;
import com.side.mvcTraffic.domain.waitUser.dto.AllowedUserResponse;
import com.side.mvcTraffic.domain.waitUser.dto.RankNumberResponse;
import com.side.mvcTraffic.domain.waitUser.dto.RegisterUserResponse;
import com.side.mvcTraffic.domain.waitUser.service.UserQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class UserQueueController {

    private final UserQueueService userQueueService;

    @PostMapping("")
    public RegisterUserResponse registerUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                                   @RequestParam(name = "user_id") Long userId) {

        return userQueueService.registerWaitQueue(queue, userId)
                .map(val -> new RegisterUserResponse(val.longValue()))
                .get();
    }

    @PostMapping("/allow")
    public Mono<AllowUserResponse> allowUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                             @RequestParam(name = "count") Long count) {
        return userQueueService.allowUser(queue, count)
                .map(allowed -> new AllowUserResponse(count, allowed));
    }

    @GetMapping("/allowed")
    public Mono<AllowedUserResponse> isAllowedUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                                   @RequestParam(name = "user_id") Long userId,
                                                   @RequestParam(name = "token") String token) {
        return userQueueService.isAllowed(queue, userId)
                .map(AllowedUserResponse::new);
    }

    @GetMapping("/rank")
    public RankNumberResponse getRankUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                                @RequestParam(name = "user_id") Long userId) {
        return new RankNumberResponse(userQueueService.getRank(queue, userId));
    }

    @GetMapping("/touch")
    Mono<?> touch(@RequestParam(name = "queue", defaultValue = "default") String queue,
                  @RequestParam(name = "user_id") Long userId) {
        return Mono.just("touch");

    }
}
