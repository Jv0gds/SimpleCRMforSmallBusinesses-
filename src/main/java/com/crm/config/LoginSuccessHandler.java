package com.crm.config;

import com.crm.model.Role;
import com.crm.model.User;
import com.crm.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * 登录成功处理器
 * 根据用户角色自动跳转至对应页面
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);

    private final UserRepository userRepository;

    public LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 处理登录成功事件
     * 根据用户角色级别决定跳转目标页面
     * 
     * 路由策略：
     * - ADMIN (level=5) → /admin/dashboard (管理员控制台)
     * - SALES_MANAGER (level=4) → /manager/dashboard (销售经理控制台)
     * - SALES_REP (level=3) → /sales/dashboard (销售代表控制台)
     * - USER (level=2) → /profile.html (个人信息页面)
     * - GUEST (level=1) → /login.html (访客需要升级账户)
     * - 无角色/未登录 → /login.html (返回登录页)
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param authentication 认证信息
     * @throws IOException IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                       HttpServletResponse response,
                                       Authentication authentication) 
            throws IOException, ServletException {
        
        String username = authentication.getName(); // 获取用户名（邮箱）
        logger.info("用户 {} 登录成功，正在确定跳转目标...", username);

        // 从数据库查询用户及其角色信息
        Optional<User> userOptional = userRepository.findByEmail(username);
        
        if (userOptional.isEmpty()) {
            logger.warn("用户 {} 登录成功但数据库中找不到记录，跳转至登录页", username);
            response.sendRedirect("/login.html");
            return;
        }

        User user = userOptional.get();
        Set<Role> roles = user.getRoles();

        // 如果用户没有角色，跳转至登录页
        if (roles == null || roles.isEmpty()) {
            logger.warn("用户 {} 没有任何角色，跳转至登录页", username);
            response.sendRedirect("/login.html");
            return;
        }

        // 获取用户的最高级别角色
        Role highestRole = roles.stream()
                .max((r1, r2) -> Integer.compare(r1.getLevel(), r2.getLevel()))
                .orElse(null);

        if (highestRole == null) {
            logger.warn("用户 {} 无法确定最高角色，跳转至登录页", username);
            response.sendRedirect("/login.html");
            return;
        }

        // 根据最高角色级别决定跳转目标
        String targetUrl = determineTargetUrl(highestRole);
        logger.info("用户 {} 的最高角色为 {} (level={}), 跳转至: {}", 
                username, highestRole.getName(), highestRole.getLevel(), targetUrl);

        // 执行重定向
        response.sendRedirect(targetUrl);
    }

    /**
     * 根据角色确定跳转目标URL
     * 
     * @param role 用户的最高级别角色
     * @return 目标URL
     */
    private String determineTargetUrl(Role role) {
        String roleName = role.getName();
        
        // 根据角色名称匹配跳转目标
        switch (roleName) {
            case "ADMIN":
                return "/admin-dashboard.html"; // ????????????.html
            
            case "SALES_MANAGER":
                return "/manager-dashboard.html"; // ????????????.html
            
            case "SALES_REP":
                return "/sales-dashboard.html"; // ????????????.html
            
            case "USER":
                return "/profile.html";
            
            case "GUEST":
                // 访客需要升级账户权限
                return "/login.html?message=guest_upgrade_required";
            
            default:
                // 未知角色，跳转至个人页面
                logger.warn("未知角色: {}, 默认跳转至个人页面", roleName);
                return "/profile.html";
        }
    }
}
