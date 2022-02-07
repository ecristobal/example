package com.example.enrich.spring;

import com.example.enrich.streams.SerdeConfiguration;
import com.example.enrich.streams.StreamConfiguration;
import com.example.enrich.validators.ValidatorsConfiguration;
import com.example.enrich.validators.ValidatorsUtilitiesConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackageClasses = {StreamConfiguration.class, SerdeConfiguration.class, ValidatorsConfiguration.class,
                ValidatorsUtilitiesConfiguration.class})
class EnrichApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnrichApplication.class, args);
    }

}
