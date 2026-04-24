package uk.gov.laa.pfla;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ProfileLogger {

    public ProfileLogger(Environment env) {
        System.out.println("ACTIVE PROFILES: " + Arrays.toString(env.getActiveProfiles()));
    }
}