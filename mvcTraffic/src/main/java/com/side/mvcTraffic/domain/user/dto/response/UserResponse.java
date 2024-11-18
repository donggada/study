package com.side.mvcTraffic.domain.user.dto.response;


import com.side.mvcTraffic.domain.user.entity.User;

public record UserResponse(Long id, String username, String loginId) {

    public static UserResponse of (User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getLoginId());
    }
}
