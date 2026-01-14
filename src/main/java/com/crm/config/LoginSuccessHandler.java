package com.crm.config;

import com.crm.dto.ApiResponse;
import com.crm.model.User;
import com.crm.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登录成功处理器
 * For API-based login, this handler returns a JSON response instead of a redirect.
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public LoginSuccessHandler(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Handles successful authentication by returning a JSON response with user details.
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     *                       the authentication process.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Authentication authentication)
            throws IOException, ServletException {

        String username = authentication.getName();
        logger.info("User '{}' logged in successfully. Preparing JSON response.", username);

        // The user is authenticated, so we can fetch their details to return in the response.
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database: " + username));

        ApiResponse<User> apiResponse = ApiResponse.success("Login successful", user);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Write the JSON response
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.getWriter().flush();
    }
}
