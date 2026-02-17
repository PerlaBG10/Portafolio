package com.parqlink.parqlink.controller;

import com.parqlink.parqlink.dto.ProfileResponse;
import com.parqlink.parqlink.entity.User;
import com.parqlink.parqlink.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ProfileResponse getUserProfile(Authentication auth) {
        String userEmail = auth.getName();
        User user = userService.getUserByEmail(userEmail).orElseThrow();
        return new ProfileResponse(userEmail,user.getName());
    }
}

