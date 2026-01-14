package com.crm.controller;

import com.crm.dto.LoginRequest;
import com.crm.dto.UpdateProfileRequest;
import com.crm.dto.UserProfileDto;
import com.crm.dto.UserProfileWithRolesDto;
import com.crm.dto.RoleDto;
import com.crm.dto.ApiResponse;
import com.crm.model.User;
import com.crm.service.UserService;
import com.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;

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

    /**
     * 获取用户个人资料接口
     * @param userId 用户ID（通过请求头传递，模拟从认证Token中获取）
     * @return 用户个人资料
     */
    @GetMapping("/user/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfile(
            @RequestHeader(value = "X-User-Id", required = true) Long userId) {
        try {
            UserProfileDto profile = userService.getUserProfile(userId);
            return ResponseEntity.ok(ApiResponse.success("获取个人资料成功", profile));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取个人资料失败：" + e.getMessage()));
        }
    }

    /**
     * 更新用户个人资料接口
     * @param userId 用户ID（通过请求头传递，模拟从认证Token中获取）
     * @param request 更新请求（包含用户名和可选的新密码）
     * @return 更新后的用户个人资料
     */
    @PutMapping("/user/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateUserProfile(
            @RequestHeader(value = "X-User-Id", required = true) Long userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        try {
            UserProfileDto profile = userService.updateUserProfile(userId, request);
            return ResponseEntity.ok(ApiResponse.success("个人资料更新成功", profile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("更新个人资料失败：" + e.getMessage()));
        }
    }

    /**
     * 获取当前登录用户的角色信息
     * @return 当前用户的角色列表
     */
    @GetMapping("/user/roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Set<RoleDto>>> getCurrentUserRoles() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            Set<RoleDto> roles = RoleDto.fromEntities(user.getRoles());
            return ResponseEntity.ok(ApiResponse.success("获取角色信息成功", roles));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取角色信息失败：" + e.getMessage()));
        }
    }

    /**
     * 获取当前登录用户的完整资料（包含角色信息）
     * @return 包含角色的用户资料
     */
    @GetMapping("/user/profile-with-roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileWithRolesDto>> getCurrentUserProfileWithRoles() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            UserProfileWithRolesDto profileWithRoles = UserProfileWithRolesDto.fromEntity(user);
            return ResponseEntity.ok(ApiResponse.success("获取用户资料成功", profileWithRoles));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取用户资料失败：" + e.getMessage()));
        }
    }

    /**
     * 检查当前用户是否拥有指定角色
     * @param roleName 角色名称（如：ADMIN, SALES_MANAGER, SALES_REP, USER）
     * @return 是否拥有该角色
     */
    @GetMapping("/user/has-role/{roleName}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Boolean>> hasRole(@PathVariable String roleName) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            boolean hasRole = user.hasRole(roleName);
            return ResponseEntity.ok(ApiResponse.success(
                    hasRole ? "用户拥有该角色" : "用户不拥有该角色",
                    hasRole
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("检查角色失败：" + e.getMessage()));
        }
    }

    /**
     * 获取所有用户列表（仅管理员可访问）
     * @return 所有用户列表
     */
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> getAllUsers() {
        // 这是一个示例接口，展示如何使用角色权限注解
        // 实际实现需要在Service层添加相应的方法
        return ResponseEntity.ok(ApiResponse.success(
                "管理员接口访问成功",
                "此接口仅管理员可访问"
        ));
    }

    /**
     * 销售经理仪表板（仅销售经理和管理员可访问）
     * @return 销售经理仪表板数据
     */
    @GetMapping("/manager/dashboard")
    @PreAuthorize("hasAnyRole('SALES_MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> getManagerDashboard() {
        return ResponseEntity.ok(ApiResponse.success(
                "销售经理仪表板",
                "此接口仅销售经理和管理员可访问"
        ));
    }

    /**
     * 销售代表仪表板（销售代表、销售经理和管理员可访问）
     * @return 销售代表仪表板数据
     */
    @GetMapping("/sales/dashboard")
    @PreAuthorize("hasAnyRole('SALES_REP', 'SALES_MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> getSalesDashboard() {
        return ResponseEntity.ok(ApiResponse.success(
                "销售代表仪表板",
                "此接口仅销售人员可访问"
        ));
    }
}
