package com.side.mvcTraffic.domain.waitUser.controller;


import com.side.mvcTraffic.domain.waitUser.service.UserQueueService;
import com.side.mvcTraffic.global.exception.ApplicationException;
import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class WaitingRoomController {
    private final UserQueueService userQueueService;

    @GetMapping("/waiting-room")
    String waitingRoomPage(@RequestParam(name = "queue", defaultValue = "default") String queue,
                           @RequestParam(name = "user_id") Long userId,
                           Model model,
                           HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String cookieName = "user-queue-%s-token".formatted(queue);

        String token = "";
        if (cookies != null) {
            var cookie = Arrays.stream(cookies).filter(i -> i.getName().equalsIgnoreCase(cookieName)).findFirst();
            token = cookie.orElse(new Cookie(cookieName, "")).getValue();
        }

//        Boolean isAllowed = userQueueService.isAllowed(queue, userId);
        Boolean isAllowed = userQueueService.isAllowedByToken(queue, userId, token);


        if (isAllowed) {
            return "waiting-main-room";
        }

        Long rank = userQueueService.registerWaitQueue(queue, userId);

        model.addAttribute("queue", queue);
        model.addAttribute("userId", userId);
        model.addAttribute("number", rank);

        return "waiting-room";
    }
}
