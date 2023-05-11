package com.schening.xinghuan.samples.redisson.controller;

import com.schening.xinghuan.samples.redisson.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/5/11 16:46
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user/create")
    public String create() {
        return userService.create();
    }

}
