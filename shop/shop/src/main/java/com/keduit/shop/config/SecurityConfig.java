package com.keduit.shop.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // 여기서 로그인을 컨트롤러에서 post방식으로 받지 않는 이유는
    // 비밀번호, 즉 보안과 연관되어 있기 때문이다
    // 비밀번호를 다룰땐 항상 post가 아닌 여기서 처리해야한다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        System.out.println("----------------- SecurityFilterChain ----------------");

        http.formLogin()
                // 로그인 처리 화면
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")   //loadUserByUsername(String email)로 실행
                .failureUrl("/members/login/error")
                .and()
                .logout()
                // 로그아웃 처리 url
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/");

//    permitAll(): 모든 사용자가 인증(로그인) 없이 해당 경로에 접근 가능
//    hasRole("ADMIN"): 관리자인 경우 /admin/으로 접근하는 경로를 통과시킴
//    anyRequest().authenticated() 위의 경우 이외의 페이지는 인증 절차가 필요.
        http.authorizeRequests()
                .mvcMatchers("/", "/members/**",
                        "/item/**", "/images/**", "error", "favicon.io").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();

//    인증되지 않은 사용자가 리소스 접근하여 실패했을 때 처리하는 핸들러 등록
        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //  /resource/static 폴더의 하위 파일은 인증에서 제외시킴.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web)-> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        //toStaticResources() 은 정적 파일. css,js,img 등 얘네들은 누구나 볼 수 있게 함?해야됌?
    }

}
