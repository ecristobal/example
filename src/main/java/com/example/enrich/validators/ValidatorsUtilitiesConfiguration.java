package com.example.enrich.validators;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableCaching
public class ValidatorsUtilitiesConfiguration {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
