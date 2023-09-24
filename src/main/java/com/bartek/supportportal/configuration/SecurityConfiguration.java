package com.bartek.supportportal.configuration;

import com.bartek.supportportal.constant.SecurityConstant;
import com.bartek.supportportal.filter.JwtAccessDeniedHandler;
import com.bartek.supportportal.filter.JwtAuthenticationEntryPoint;
import com.bartek.supportportal.filter.JwtAuthorizationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfiguration {
  private JwtAuthorizationFilter authorizationFilter;
  private JwtAccessDeniedHandler accessDeniedHandler;
  private JwtAuthenticationEntryPoint authenticationEntryPoint;
  private UserDetailsService userDetailsService;
  private BCryptPasswordEncoder passwordEncoder;
  private AuthenticationEventPublisher authenticationEventPublisher;

  /*~~(Migrate manually based on https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)~~>*/
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    auth.authenticationEventPublisher(authenticationEventPublisher);
  }

//  @Bean
//  protected AuthenticationManager authenticationManager() throws Exception {
//    return super.authenticationManager();
//  }

  @Bean
  public AuthenticationManager authenticationManager(){
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(authProvider);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .cors()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeHttpRequests()
        .requestMatchers(SecurityConstant.PUBLIC_URL)
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler)
        .authenticationEntryPoint(authenticationEntryPoint)
        .and()
        .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
