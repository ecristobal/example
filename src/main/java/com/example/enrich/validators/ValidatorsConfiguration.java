package com.example.enrich.validators;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({CollaboratorValidatorProperties.class, ConsentValidatorProperties.class})
public class ValidatorsConfiguration {

    @Bean
    ConsentValidator consentValidator(final RestTemplate restTemplate, final ConsentValidatorProperties properties) {
        return new ConsentValidator(restTemplate, properties);
    }

    @Bean
    CollaboratorValidator collaboratorValidator(
            final RestTemplate restTemplate, final CollaboratorValidatorProperties properties
    ) {
        return new CollaboratorValidator(restTemplate, properties);
    }

}
