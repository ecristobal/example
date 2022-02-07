package com.example.enrich.messages;

import java.io.Serializable;
import java.util.Map;

public record PurchaseFullyEnriched(
        Purchase purchase,
        Map<Long, Integer> itemProperties,
        boolean isCollaborator
) implements Serializable {

    public PurchaseFullyEnriched(
            final UnformattedEnrichedPurchase unformattedEnrichedPurchase, final boolean isCollaborator
    ) {
        this(unformattedEnrichedPurchase.purchase(), unformattedEnrichedPurchase.itemProperties(), isCollaborator);
    }

}
