package com.example.enrich.streams;

import com.example.enrich.builders.EnrichedPurchaseBuilder;
import com.example.enrich.messages.EnrichedPurchase;
import com.example.enrich.messages.OneCatalogItem;
import com.example.enrich.messages.Purchase;
import com.example.enrich.messages.PurchaseFullyEnriched;
import com.example.enrich.streams.transformers.PurchaseTransformer;
import com.example.enrich.validators.Validator;
import java.util.UUID;
import java.util.function.BiFunction;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StreamConfiguration {

    @Bean
    public BiFunction<KStream<UUID, Purchase>, GlobalKTable<Long, OneCatalogItem>, KStream<UUID, EnrichedPurchase>> enrich(
            final Validator<UUID> consentValidator, final Validator<UUID> memberValidator
    ) {
        //@formatter:off
        return (purchaseStream, catalogTable) -> purchaseStream
                .filter((id, purchase) -> consentValidator.validate(purchase.memberUuid()))
                .transformValues(() -> new PurchaseTransformer())
                .mapValues(unformattedEnrichedPurchase -> {
                    final boolean isMember = memberValidator
                            .validate(unformattedEnrichedPurchase.purchase().memberUuid());
                    return new PurchaseFullyEnriched(unformattedEnrichedPurchase, isMember);
                })
                .mapValues(EnrichedPurchaseBuilder::build);
        //@formatter:on
    }

}
