package com.example.enrich.builders;

import com.example.enrich.messages.EnrichedPurchase;
import com.example.enrich.messages.EnrichedPurchase.EnrichedTransaction;
import com.example.enrich.messages.EnrichedPurchase.EnrichedTransaction.EnrichedSaleItem;
import com.example.enrich.messages.Purchase;
import com.example.enrich.messages.Purchase.Transaction;
import com.example.enrich.messages.Purchase.Transaction.SaleItem;
import com.example.enrich.messages.PurchaseFullyEnriched;
import java.util.List;
import java.util.Map;

public class EnrichedPurchaseBuilder {

    private EnrichedPurchaseBuilder() {
        // Empty constructor
    }

    public static EnrichedPurchase build(
            final PurchaseFullyEnriched purchaseFullyEnriched
    ) {
        final Purchase purchase = purchaseFullyEnriched.purchase();
        final Map<Long, Integer> newProperties = purchaseFullyEnriched.itemProperties();
        final boolean isCollaborator = purchaseFullyEnriched.isCollaborator();
        final EnrichedTransaction enrichedTransaction =
                EnrichedPurchaseBuilder.build(purchase.transaction(), newProperties);
        return new EnrichedPurchase(purchase.id(), purchase.memberUuid(), isCollaborator, purchase.source(),
                purchase.operator(), purchase.creationDateTime(), purchase.updateDateTime(),
                purchase.associationDateTime(), enrichedTransaction);
    }

    private static EnrichedTransaction build(
            final Transaction transaction, final Map<Long, Integer> itemProperties
    ) {
        //@formatter:off
        final List<EnrichedSaleItem> soldItems = transaction.saleItems().parallelStream()
                .map(saleItem -> EnrichedPurchaseBuilder.build(saleItem, itemProperties.get(saleItem.itemId()))).toList();
        final List<EnrichedSaleItem> returnedItems = transaction.returnItems().parallelStream()
                .map(saleItem -> EnrichedPurchaseBuilder.build(saleItem, itemProperties.get(saleItem.itemId()))).toList();
        //@formatter:on
        return new EnrichedTransaction(transaction.transactionId(), transaction.transactionType(),
                transaction.customerId(), transaction.businessUnitId(), transaction.businessUnitType(),
                transaction.countryId(), transaction.countryName(), transaction.currency(),
                transaction.transactionDateTime(), transaction.messageDateTime(), transaction.orderNumber(),
                transaction.amount(), transaction.netAmount(), transaction.taxAmount(), transaction.discountAmount(),
                transaction.shippingFee(), soldItems, returnedItems, transaction.tender(), transaction.payload());
    }

    private static EnrichedSaleItem build(final SaleItem saleItem, final Integer newProperty) {
        return new EnrichedSaleItem(saleItem.itemId(), saleItem.categoryId(), saleItem.unitPrice(), saleItem.quantity(),
                saleItem.amount(), saleItem.discountAmount(), saleItem.priceModifications(), saleItem.taxLines(),
                saleItem.sku(), saleItem.partnerId(), newProperty);
    }

}
