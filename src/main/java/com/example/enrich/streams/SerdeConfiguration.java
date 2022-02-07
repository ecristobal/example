package com.example.enrich.streams;

import com.example.enrich.messages.EnrichedPurchase;
import com.example.enrich.messages.OneCatalogItem;
import com.example.enrich.messages.Purchase;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
public class SerdeConfiguration {

    @Bean
    Serde<OneCatalogItem> oneCatalogItemSerde() {
        return new JsonSerde<>(OneCatalogItem.class);
    }

    @Bean
    Serde<Purchase> purchaseSerde() {
        return new JsonSerde<>(Purchase.class);
    }

    @Bean
    Serde<EnrichedPurchase> enrichedPurchaseSerde() {
        return new JsonSerde<>(EnrichedPurchase.class);
    }

}
