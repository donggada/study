package com.side.mvcTraffic.domain.waitUser.controller;


import com.side.mvcTraffic.domain.waitUser.dto.AllowUserResponse;
import com.side.mvcTraffic.domain.waitUser.dto.AllowedUserResponse;
import com.side.mvcTraffic.domain.waitUser.dto.RankNumberResponse;
import com.side.mvcTraffic.domain.waitUser.dto.RegisterUserResponse;
import com.side.mvcTraffic.domain.waitUser.service.UserQueueService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class UserQueueController {

    private final UserQueueService userQueueService;

    @PostMapping("")
    public RegisterUserResponse registerUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                                   @RequestParam(name = "user_id") Long userId) {

        return new RegisterUserResponse(userQueueService.registerWaitQueue(queue, userId));
    }

    @PostMapping("/allow")
    public AllowUserResponse allowUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                       @RequestParam(name = "count") Long count) {
        return new AllowUserResponse(count, userQueueService.allowUser(queue, count));
    }

    @GetMapping("/allowed")
    public AllowedUserResponse isAllowedUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                                   @RequestParam(name = "user_id") Long userId,
                                                   @RequestParam(name = "token") String token) {
        return new AllowedUserResponse(userQueueService.isAllowed(queue, userId));

    }

    @GetMapping("/rank")
    public RankNumberResponse getRankUser(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                                @RequestParam(name = "user_id") Long userId) {
        return new RankNumberResponse(userQueueService.getRank(queue, userId));
    }

    @GetMapping("/touch")
    String touch(@RequestParam(name = "queue", defaultValue = "default") String queue,
                  @RequestParam(name = "user_id") Long userId,
                 HttpServletResponse response) {
        String token = userQueueService.generateToken(queue, userId);
        Cookie cookie = new Cookie("user-queue-%s-token".formatted(queue), token);
        cookie.setPath("/");
        cookie.setMaxAge(300);
        response.addCookie(cookie);
        return token;
    }
}
