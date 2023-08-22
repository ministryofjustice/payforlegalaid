package uk.gov.laa.pfla.auth.service;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PflaApplication {

    //Maps objects (such as model objects -> response objects), using the names of the object fields.
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(PflaApplication.class, args);
    }

}
