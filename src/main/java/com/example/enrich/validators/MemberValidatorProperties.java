package com.example.enrich.validators;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("validators.member")
public record MemberValidatorProperties(
        String host,
        String endpoint
) {}
