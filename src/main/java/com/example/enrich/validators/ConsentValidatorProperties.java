package com.example.enrich.validators;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("validators.consent")
public record ConsentValidatorProperties(
        String host,
        String endpoint
) {}
