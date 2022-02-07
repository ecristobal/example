package com.example.enrich.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record Purchase(
        @JsonProperty("id") UUID id,
        @JsonProperty("member_uuid") UUID memberUuid,
        @JsonProperty("source") String source,
        @JsonProperty("operator") String operator,
        @JsonProperty("creation_data_time") ZonedDateTime creationDateTime,
        @JsonProperty("update_date_time") ZonedDateTime updateDateTime,
        @JsonProperty("association_date_time") ZonedDateTime associationDateTime,
        @JsonProperty("transaction") Transaction transaction
) implements Serializable {

    public record Transaction(
            @JsonProperty("transaction_id") UUID transactionId,
            @JsonProperty("transaction_type") String transactionType,
            @JsonProperty("customer_id") Long customerId,
            @JsonProperty("business_unit_id") String businessUnitId,
            @JsonProperty("business_unit_type") String businessUnitType,
            @JsonProperty("country_id") String countryId,
            @JsonProperty("country_name") String countryName,
            @JsonProperty("currency") String currency,
            @JsonProperty("transaction_date_time") ZonedDateTime transactionDateTime,
            @JsonProperty("message_date_time") ZonedDateTime messageDateTime,
            @JsonProperty("order_number") String orderNumber,
            @JsonProperty("amount") Double amount,
            @JsonProperty("net_amount") Double netAmount,
            @JsonProperty("tax_amount") Double taxAmount,
            @JsonProperty("discount_amount") Double discountAmount,
            @JsonProperty("shipping_fee") Double shippingFee,
            @JsonProperty("sale_items") List<SaleItem> saleItems,
            @JsonProperty("return_items") List<SaleItem> returnItems,
            @JsonProperty("tender") List<Tender> tender,
            @JsonProperty("payload") String payload
    ) {

        public record Tender(
                @JsonProperty("type") String type,
                @JsonProperty("type_code") String typeCode,
                @JsonProperty("amount") Double amount
        ) {}

        public record SaleItem(
                @JsonProperty("item_id") Long itemId,
                @JsonProperty("category_id") String categoryId,
                @JsonProperty("unit_price") Double unitPrice,
                @JsonProperty("quantity") Integer quantity,
                @JsonProperty("amount") Double amount,
                @JsonProperty("discount_amount") Double discountAmount,
                @JsonProperty("price_modifications") List<Double> priceModifications,
                @JsonProperty("tax_lines") List<TaxLine> taxLines,
                @JsonProperty("sku") UUID sku,
                @JsonProperty("partner_id") UUID partnerId
        ) {

            public record TaxLine(
                    @JsonProperty("type_code") String typeCode,
                    @JsonProperty("sub_type_code") String subTypeCode,
                    @JsonProperty("taxable_amount") Double taxableAmount,
                    @JsonProperty("amount") Double amount,
                    @JsonProperty("rate") Double rate
            ) {}

        }

    }

}
