package com.example.enrich.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OneCatalogItem(
        @JsonProperty("isDeleted") Boolean isDeleted,
        @JsonProperty("subjectOf") List<SubjectOf> subjectOf,
        @JsonProperty("weight") Weight weight,
        @JsonProperty("brand") Brand brand,
        @JsonProperty("name") String name,
        @JsonProperty("model") Model model,
        @JsonProperty("@type") String type,
        @JsonProperty("additionalProperty") List<AdditionalProperty> additionalProperties,
        @JsonProperty("description") String description
) {

    public record SubjectOf(
            @JsonProperty("inLanguage") String inLanguage,
            @JsonProperty("identifier") String identifier,
            @JsonProperty("keywords") String keywords,
            @JsonProperty("@type") String type
    ) {}

    public record Weight(
            @JsonProperty("value") Double value,
            @JsonProperty("unitText") String unitText,
            @JsonProperty("@type") String type
    ) {}

    public record Brand(
            @JsonProperty("identifier") String identifier,
            @JsonProperty("@type") String type
    ) {}

    public record Model(
            @JsonProperty("@type") String type
    ) {}

    public record AdditionalProperty(
            @JsonProperty("value") List<Object> value,
            @JsonProperty("propertyID") String propertyID,
            @JsonProperty("@type") String type
    ) {}

}
