package com.side.webFluxTraffic.domain.waitUser.controller;


import com.side.webFluxTraffic.domain.waitUser.service.UserQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Controller
@RequiredArgsConstructor
public class WaitingRoomController {
    private final UserQueueService userQueueService;


    @GetMapping("/waiting-room")
    Mono<Rendering> waitingRoomPage(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                    @RequestParam(name = "user_id") Long userId,
                                    ServerWebExchange exchange) {
        var redirectUrl = "/waiting-main-room?queue=%s&user_id=%s".formatted(queue, userId);
        var token = getToken(queue, exchange);

        return userQueueService.isAllowedByToken(queue, userId, token)
                .filter(allowed -> allowed)
                .flatMap(allowed -> Mono.just(Rendering.redirectTo(redirectUrl).build()))
                .switchIfEmpty(
                        userQueueService.registerWaitQueue(queue, userId)
                                .onErrorResume(ex -> userQueueService.getRank(queue, userId))
                                .map(rank -> Rendering.view("waiting-room.html")
                                        .modelAttribute("number", rank)
                                        .modelAttribute("userId", userId)
                                        .modelAttribute("queue", queue)
                                        .build()
                                )
                );
    }

    @GetMapping("/waiting-main-room")
    Mono<Rendering> waitingMainRoomPage(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                        @RequestParam(name = "user_id") Long userId,
                                        ServerWebExchange exchange
    ) {
        var token = getToken(queue, exchange);

        Rendering waitingRoomRendering = Rendering.view("waiting-room.html")
                .modelAttribute("userId", userId)
                .modelAttribute("queue", queue)
                .build();

        if (exchange.getRequest().getBody() == null) {
            // 대기 웹페이지로 리다이렉트
            return Mono.just(waitingRoomRendering);
        }

        return userQueueService.isAllowedByToken(queue, userId, token).filter(allowed -> allowed)
                .flatMap(allowed -> Mono.just(Rendering.view("waiting-main-room.html").build()))
                .switchIfEmpty(Mono.just(waitingRoomRendering));
    }

    private String getToken(String queue, ServerWebExchange exchange) {
        var key = "user-queue-%s-token".formatted(queue);
        var cookieValue = exchange.getRequest().getCookies().getFirst(key);
        var token = (cookieValue == null) ? "" : cookieValue.getValue();
        return token;
    }

}
