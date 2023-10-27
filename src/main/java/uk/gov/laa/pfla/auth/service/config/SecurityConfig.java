// todo: uncomment class
 package uk.gov.laa.pfla.auth.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import com.azure.core.client.traits.HttpTrait;

//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {


    return http
            // you probably want a request matcher since you are using @Order
            .authorizeHttpRequests(authorize -> authorize
                    .anyRequest().authenticated()
            ).build();



//     return http
//          .authorizeHttpRequests()
//  //        .antMatchers("/ssologout.do").permitAll()
//  //        .antMatchers("/logout").denyAll()
//          .anyRequest()
//          .authenticated()
//          .build();
  //        .and()
  //        .logout()
  //        .logoutUrl("/ssologout.do")
  //        .logoutSuccessUrl("/")
  //        .permitAll();
    }
}
