package com.side.mvcTraffic.domain.user.entity;


import com.side.mvcTraffic.domain.user.dto.request.UserRegisterRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users", indexes = @Index(name = "idx_user_loginId", columnList = "loginId"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true)
    private String loginId;

    private String password; // 암호화가 필요함


    private User(String username, String loginId, String password) {
        this.id = 0L;
        this.username = username;
        this.loginId = loginId;
        this.password = password;
    }

    public static User of(UserRegisterRequest userRegisterRequest, String password) {
        return new User(userRegisterRequest.getUsername(), userRegisterRequest.getLoginId(), password);
    }

}

