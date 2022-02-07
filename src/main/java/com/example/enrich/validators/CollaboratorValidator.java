package com.example.enrich.validators;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;

class CollaboratorValidator implements Validator<UUID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollaboratorValidator.class);

    private final RestTemplate restTemplate;

    private final String url;

    public CollaboratorValidator(final RestTemplate restTemplate, final CollaboratorValidatorProperties properties) {
        this.restTemplate = restTemplate;
        this.url = properties.host().concat(properties.endpoint());
    }

    @Override
    @Cacheable("collaboratorCache")
    public boolean validate(final UUID memberId) {
        return this.restTemplate.getForObject(this.url, Boolean.class, memberId);
    }

}
