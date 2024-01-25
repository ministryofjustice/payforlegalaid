package uk.gov.laa.pfla.auth.service;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class PflaApplication {

    //Maps objects (such as model objects -> response objects), using the names of the object fields.
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new ByteArrayHttpMessageConverter());

        restTemplate.setMessageConverters(messageConverters);

        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(PflaApplication.class, args);
    }

}
