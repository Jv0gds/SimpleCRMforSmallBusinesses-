package com.crm.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "An error occurred. Please try again later.";

        if (exception instanceof UsernameNotFoundException) {
            errorMessage = "User not found.";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid credentials.";
        } else if (exception instanceof DisabledException) {
            errorMessage = "User is disabled.";
        }

        response.sendRedirect(request.getContextPath() + "/login.html?error=" + errorMessage);
    }
}
