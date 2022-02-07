package com.example.enrich.messages;

import java.io.Serializable;
import java.util.Map;

public record PurchaseFullyEnriched(
        Purchase purchase,
        Map<Long, Integer> itemProperties,
        boolean isMember
) implements Serializable {

    public PurchaseFullyEnriched(
            final PurchaseWithProperties purchaseWithProperties, final boolean isCollaborator
    ) {
        this(purchaseWithProperties.purchase(), purchaseWithProperties.itemProperties(), isCollaborator);
    }

}
