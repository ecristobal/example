package com.example.enrich.streams.transformers;

import com.example.enrich.messages.OneCatalogItem;
import com.example.enrich.messages.Purchase;
import com.example.enrich.messages.Purchase.Transaction.SaleItem;
import com.example.enrich.messages.UnformattedEnrichedPurchase;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import static java.util.stream.Collectors.toMap;

public class PurchaseTransformer implements ValueTransformerWithKey<UUID, Purchase, UnformattedEnrichedPurchase> {

    private static final String ECO_LEVEL_TAG = "NEW_PROPERTY";

    private ReadOnlyKeyValueStore<Long, OneCatalogItem> itemStore;

    @Override
    public void init(final ProcessorContext context) {
        this.itemStore = (ReadOnlyKeyValueStore<Long, OneCatalogItem>) context.getStateStore("item-store");
    }

    @Override
    public UnformattedEnrichedPurchase transform(final UUID id, final Purchase purchase) {
        final Map<Long, Integer> newProperties =
                Stream.of(purchase.transaction().saleItems(), purchase.transaction().returnItems())
                      .flatMap(Collection::parallelStream)
                      .map(SaleItem::itemId)
                      .distinct()
                      .map(itemId -> {
                          final OneCatalogItem item = this.itemStore.get(itemId);
                          final Integer newProperty = item.additionalProperties()
                                                       .parallelStream()
                                                       .filter(property ->
                                                               ECO_LEVEL_TAG.equals(property.propertyID()) &&
                                                               ! property.value().isEmpty())
                                                       .map(property -> Integer.valueOf(
                                                               property.value().get(0).toString()))
                                                       .findFirst()
                                                       .orElse(Integer.MAX_VALUE);
                          return Map.entry(itemId, newProperty);
                      })
                      .filter(entry -> entry.getValue() != Integer.MAX_VALUE)
                      .collect(toMap(Entry::getKey, Entry::getValue));
        return new UnformattedEnrichedPurchase(purchase, newProperties);
    }

    @Override
    public void close() {

    }

}
