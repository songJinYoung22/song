package com.keduit.shop.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // ajax 비동기 통신의 경우 http request header 에 XMLHttpRequest라는 값을 넣어줌.
        // 이때 인증되지 않은 사용자 (로그인 안한) 가 ajax로 리소스 요청을 한 경우 401
        // 나머지는 로그인을 유도 (리다이렉트)함

        // 만약 비동기통신으로 데이터를 주고 싶다면 토큰을 받고 체크하는 식으로 코드를 추가해야함.
        if("XMLHttpRequest".equals(request.getHeader("x-requested-with"))) {
            response.sendError((HttpServletResponse.SC_UNAUTHORIZED), "Unauthorized");
        }else{
            response.sendRedirect("/members/login");
        }
    }
}