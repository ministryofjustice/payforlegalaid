package uk.gov.laa.pfla.auth.service.config;

import com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadWebApplicationHttpSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration(proxyBeanMethods = false)
//@EnableWebSecurity
//@EnableMethodSecurity
//public class AadOAuth2LoginSecurityConfig {
//
//    /**
//     * Add configuration logic as needed.
//     */
//    @Bean
//    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.apply(AadWebApplicationHttpSecurityConfigurer.aadWebApplication())
//                .and()
//                .authorizeHttpRequests()
//                .anyRequest().authenticated();
//        // Do some custom configuration.
//        return http.build();
//    }
//}



@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
public class AadOAuth2LoginSecurityConfig  {

    /**
     * Add configuration logic as needed.
     */
    @Bean
    public SecurityFilterChain htmlFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf(csrf -> csrf.disable())
                .apply(AadWebApplicationHttpSecurityConfigurer.aadWebApplication())
                .and()
                .oauth2Login()
//                .loginProcessingUrl("http://localhost:8080/login/oauth2/code/gpfd-azure-dev")
                .and()
                .authorizeHttpRequests()
                .anyRequest().authenticated();
        // @formatter:on
        return http.build();
    }
}