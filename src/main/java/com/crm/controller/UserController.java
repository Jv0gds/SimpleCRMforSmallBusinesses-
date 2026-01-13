package com.crm.controller;

import com.crm.dto.LoginRequest;
import com.crm.dto.ApiResponse;
import com.crm.model.User;
import com.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     * @param user 用户信息
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("注册成功", registeredUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("注册失败：" + e.getMessage()));
        }
    }

    /**
     * 用户登录接口
     * @param loginRequest 包含邮箱和密码的登录请求
     * @return 登录成功的用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(ApiResponse.success("登录成功", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("登录失败：" + e.getMessage()));
        }
    }

    /**
     * 用户注销接口（当前为简单实现，后续可扩展 Token 失效逻辑）
     * @return 注销成功响应
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success("注销成功", null));
    }
}
