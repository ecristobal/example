package com.example.enrich.messages;

import java.io.Serializable;
import java.util.Map;

public record UnformattedEnrichedPurchase(
        Purchase purchase,
        Map<Long, Integer> itemProperties
) implements Serializable {}
