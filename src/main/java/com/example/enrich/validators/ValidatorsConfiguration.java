package com.example.enrich.validators;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({MemberValidatorProperties.class, ConsentValidatorProperties.class})
public class ValidatorsConfiguration {

    @Bean
    ConsentValidator consentValidator(final RestTemplate restTemplate, final ConsentValidatorProperties properties) {
        return new ConsentValidator(restTemplate, properties);
    }

    @Bean
    MemberValidator memberValidator(
            final RestTemplate restTemplate, final MemberValidatorProperties properties
    ) {
        return new MemberValidator(restTemplate, properties);
    }

}
