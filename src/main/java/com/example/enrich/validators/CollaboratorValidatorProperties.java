package com.example.enrich.validators;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("validators.collaborator")
public record CollaboratorValidatorProperties(
        String host,
        String endpoint
) {}
