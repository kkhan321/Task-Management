package com.taskservice.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class ApplicationConfig {

	//session creation policy is nothing but when we use spring secuirty all the ends points 
	// get secure so spring secuirty provides his passowrd and username when we enters its stores in local browser as session and 
	// so we will use our own session creation through jwt token, we will genterate token and stores in local its like we are telling 
	//spring dont use your we will handle by ourself
	//Stateless = No memory of previous requests.
	//It’s like talking to someone new every time — they don’t remember who you are.
	//	SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
	//		http.sessionManagement(
	//				management->management.sessionCreationPolicy(
	// SessionCreationPolicy.STATELESS)
	//				).authorizeHttpRequests(
	//						authorize->authorize.requestMatchers("/api/**").authenticated().anyRequest().permitAll()
	//				).addFilter(null,BasicAuthenticationFilter.class)
	//		.csrf(csrf->csrf.disable()).cors(cors->cors.conf)
	//	}
	@Bean
	 SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						/* .requestMatchers("/auth/signup", "/auth/login").permitAll() */
						.requestMatchers("/api/**").authenticated()
						.anyRequest().permitAll())
				.addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
				.csrf(csrf -> csrf.disable())
				.cors(cors->cors.configurationSource(corsConfigurationSource()))// uses default cors configuration
				.httpBasic(Customizer.withDefaults())
				.formLogin(Customizer.withDefaults())
				.build();
	}
    
	private CorsConfigurationSource corsConfigurationSource() {
		return new CorsConfigurationSource() {

			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration cfg=new CorsConfiguration();
				cfg.setAllowedOrigins(Collections.singletonList("*"));//avilable for all urls 
				cfg.setAllowedMethods(Collections.singletonList("*"));//avilable for all urls 
				cfg.setAllowedHeaders(Collections.singletonList("*"));//avilable for all urls 
				cfg.setAllowCredentials(true);
				cfg.setExposedHeaders(Collections.singletonList("Authorization"));
				cfg.setMaxAge(3600l);        
				return cfg;
			}

		};
	}
	
	@Bean
	public  PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
