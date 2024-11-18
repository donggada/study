package com.side.mvcTraffic.domain.waitUser.controller;


import com.side.mvcTraffic.domain.waitUser.service.UserQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class WaitingRoomController {
    private final UserQueueService userQueueService;

    @GetMapping("/waiting-room")
    String waitingRoomPage(@RequestParam(name = "queue", defaultValue = "default") String queue,
                           @RequestParam(name = "user_id") Long userId,
                           Model model) {

        Boolean isAllowed = userQueueService.isAllowed(queue, userId).block();


        if (isAllowed) {
            return "waiting-main-room";
        }

        Long rank = userQueueService.registerWaitQueue(queue, userId).filter(value -> value == -1).map(op ->
                userQueueService.getRank(queue, userId)
        ).orElse(-1L);

        model.addAttribute("queue", queue);
        model.addAttribute("userId", userId);
        model.addAttribute("rank", rank);

        return "waiting-room";
    }
}
