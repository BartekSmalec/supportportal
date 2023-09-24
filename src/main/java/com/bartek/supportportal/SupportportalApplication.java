package com.bartek.supportportal;

import static com.bartek.supportportal.constant.FileConstant.USER_FOLDER;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@Configuration
@EnableWebMvc
public class SupportportalApplication {
  @Value("${frontend.url}")
  private String frontendUrl;

  public static void main(String[] args) {
    SpringApplication.run(SupportportalApplication.class, args);
    new File(USER_FOLDER).mkdirs();
  }

  @Bean
  BCryptPasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationEventPublisher authenticationEventPublisher(
      ApplicationEventPublisher applicationEventPublisher) {
    return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
  }

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource =
        new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setAllowedOrigins(Arrays.asList(frontendUrl));
    corsConfiguration.setAllowedHeaders(
        Arrays.asList(
            "Origin",
            "Access-Control-Allow-Origin",
            "Content-Type",
            "Accept",
            "Jwt-Token",
            "Authorization",
            "Origin, Accept",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"));
    corsConfiguration.setExposedHeaders(
        Arrays.asList(
            "Origin",
            "Content-Type",
            "Accept",
            "Jwt-Token",
            "Authorization",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"));
    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

    return new CorsFilter(urlBasedCorsConfigurationSource);
  }

//  @Bean
//  public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
//      WebEndpointsSupplier webEndpointsSupplier,
//      ServletEndpointsSupplier servletEndpointsSupplier,
//      ControllerEndpointsSupplier controllerEndpointsSupplier,
//      EndpointMediaTypes endpointMediaTypes,
//      CorsEndpointProperties corsProperties,
//      WebEndpointProperties webEndpointProperties,
//      Environment environment) {
//    List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
//    Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
//    allEndpoints.addAll(webEndpoints);
//    allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
//    allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
//    String basePath = webEndpointProperties.getBasePath();
//    EndpointMapping endpointMapping = new EndpointMapping(basePath);
//    boolean shouldRegisterLinksMapping =
//        this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
//    return new WebMvcEndpointHandlerMapping(
//        endpointMapping,
//        webEndpoints,
//        endpointMediaTypes,
//        corsProperties.toCorsConfiguration(),
//        new EndpointLinksResolver(allEndpoints, basePath),
//        shouldRegisterLinksMapping,
//        null);
//  }

  private boolean shouldRegisterLinksMapping(
      WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
    return webEndpointProperties.getDiscovery().isEnabled()
        && (StringUtils.hasText(basePath)
            || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
  }
}
