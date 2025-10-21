package com.mycompany.sbbpjboard.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration // 환경 설정 파일
@EnableWebSecurity // 모든 요청 url이 스프링 시큐리티의 제어를 받도록 하는 annotation
public class Securityconfig {
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		.csrf(csrt -> csrt.disable()) // csrf 인증을 비활성화 (프론트엔드 + 백엔드 구조이기에 불필요)
		.cors(Customizer.withDefaults()) // CORS를 활성화
		.authorizeHttpRequests(auth -> auth
				// ** 권한 수정 (리액트 경로 허용)
                .requestMatchers(
                      "/", 
                      "/index.html", 
                      "/login", 
                      "/signup", 
                      "/board/**", 
                      "/static/**")
                .permitAll()                   
                  // 읽기 API는 로그인 없이 허용
                  .requestMatchers(
                        "/api/board", 
                        "/api/board/**", 
                        "/api/comments", 
                        "/api/comments/**")
                  .permitAll()
//                  // 쓰기/수정/삭제 API는 인증 필요
//                  .requestMatchers(
//                        "/api/board/write", 
//                        "/api/board/update/**", 
//                        "/api/board/delete/**")
//                  .authenticated()
//                  .requestMatchers(
//                        "/api/comments/write",
//                        "/api/comments/update/**",
//                        "/api/comments/delete/**")
//                  .authenticated()           
              .anyRequest().authenticated()
          )
		.formLogin(login -> login // 타임리프, JSP는 form에서 넘어오지만, 리액트(REST api 요청으로 오게 되면 login으로 작성해줘야 함)
				.loginPage("/login").permitAll() // ***코드 추가
				.loginProcessingUrl("/api/auth/login") // 로그인 요청 url
				.usernameParameter("username") // 아이디 input name="username"일 때
		        .passwordParameter("password") // password input name="password"일 때
		        // 값들이 SecurityService 클래스로 넘어와서 db에 있는지 없는지 확인
		        .successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK)) // 로그인 성공 시 200 (status값)
		        .failureHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))) 
		.logout(logout -> logout
				.logoutUrl("/api/auth/logout")
				.logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
				);
		return http.build();
	}
	
	@Bean 
	PasswordEncoder passwordEncoder() { // 비밀번호 암호화
		return new BCryptPasswordEncoder();
	}
	
   @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
	
	@Bean
    CorsConfigurationSource corsConfigurationSource() { // 프론트엔드 리액트에서 요청하는 주소 허용
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", // React 개발 서버
        		"http://localhost:8888",
        		"http://172.30.1.99:3000", // 배포 시 아이피 주소 넣어주기
        		"http://172.30.1.99:8888",
        		"http://172.0.0.1:3000")); 
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 쿠키, 세션 허용 시 필요

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
	

}
