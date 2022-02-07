package com.example.enrich.spring;

import com.example.enrich.messages.EnrichedPurchase;
import com.example.enrich.messages.OneCatalogItem;
import com.example.enrich.messages.OneCatalogItem.AdditionalProperty;
import com.example.enrich.messages.OneCatalogItem.Brand;
import com.example.enrich.messages.OneCatalogItem.Model;
import com.example.enrich.messages.OneCatalogItem.SubjectOf;
import com.example.enrich.messages.OneCatalogItem.Weight;
import com.example.enrich.messages.Purchase;
import com.example.enrich.messages.Purchase.Transaction;
import com.example.enrich.messages.Purchase.Transaction.SaleItem;
import com.example.enrich.messages.Purchase.Transaction.SaleItem.TaxLine;
import com.example.enrich.messages.Purchase.Transaction.Tender;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serdes.LongSerde;
import org.apache.kafka.common.serialization.Serdes.UUIDSerde;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(EmbeddedRedisTestConfiguration.class)
public class StreamFunctionIT {

    private static final String CONSENT_URL = "http://localhost:16001/consent/{id}";
    private static final String COLLABORATOR_URL = "http://localhost:16001/collaborator/{id}";

    private static final String INPUT_TOPIC = "purchases";
    private static final String OUTPUT_TOPIC = "transactions";
    private static final String CATALOG_TOPIC = "items";

    private static final int TIMEOUT = 10000;

    private static final long ITEM_ID = 12345L;
    private static final int ECO_LEVEL = 2;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule =
            new EmbeddedKafkaRule(3, true, INPUT_TOPIC, OUTPUT_TOPIC, CATALOG_TOPIC);

    public static EmbeddedKafkaBroker embeddedKafka = embeddedKafkaRule.getEmbeddedKafka();

    public static Consumer<UUID, EnrichedPurchase> consumer;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeClass
    public static void setUp() {
        final Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("group", "false", embeddedKafka);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        final DefaultKafkaConsumerFactory<UUID, EnrichedPurchase> cf =
                new DefaultKafkaConsumerFactory<>(consumerProps, new UUIDSerde().deserializer(),
                        new JsonSerde<>(EnrichedPurchase.class).deserializer());
        consumer = cf.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, OUTPUT_TOPIC);
        System.setProperty("spring.cloud.stream.kafka.streams.binder.brokers", embeddedKafka.getBrokersAsString());
        // Populate catalog stream
        final Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka);
        final DefaultKafkaProducerFactory<Long, OneCatalogItem> catalogPf =
                new DefaultKafkaProducerFactory<>(senderProps, new LongSerde().serializer(),
                        new JsonSerde<>(OneCatalogItem.class).serializer());
        final KafkaTemplate<Long, OneCatalogItem> catalogTemplate = new KafkaTemplate<>(catalogPf, true);
        catalogTemplate.setDefaultTopic(CATALOG_TOPIC);
        catalogTemplate.sendDefault(ITEM_ID, buildCatalogItem(ECO_LEVEL));
    }

    @AfterClass
    public static void tearDown() {
        consumer.close();
        System.clearProperty("spring.cloud.stream.kafka.streams.binder.brokers");
    }

    @Test
    public void testEnrichPurchase() {
        final Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka);
        final DefaultKafkaProducerFactory<UUID, Purchase> purchasePf =
                new DefaultKafkaProducerFactory<>(senderProps, new UUIDSerde().serializer(),
                        new JsonSerde<>(Purchase.class).serializer());
        try {
            final UUID customerId = UUID.randomUUID();
            final UUID purchaseId = UUID.randomUUID();
            // Mock set-up
            when(this.restTemplate.getForObject(CONSENT_URL, Boolean.class, customerId)).thenReturn(true);
            when(this.restTemplate.getForObject(COLLABORATOR_URL, Boolean.class, customerId)).thenReturn(false);
            // Send purchase
            final Purchase purchase = this.buildPurchase(customerId, ITEM_ID);
            final KafkaTemplate<UUID, Purchase> purchaseTemplate = new KafkaTemplate<>(purchasePf, true);
            purchaseTemplate.setDefaultTopic(INPUT_TOPIC);
            purchaseTemplate.sendDefault(purchaseId, purchase);
            // Get output and check content
            final ConsumerRecord<UUID, EnrichedPurchase> output =
                    KafkaTestUtils.getSingleRecord(consumer, OUTPUT_TOPIC, TIMEOUT);
            assertNotNull(output);
        } finally {
            purchasePf.destroy();
        }
    }

    private static OneCatalogItem buildCatalogItem(final int newProperty) {
        final AdditionalProperty additionalProperty =
                new AdditionalProperty(List.of(newProperty), "NEW_PROPERTY", "int");
        return new OneCatalogItem(false, List.of(new SubjectOf("es", "id", "", "")), new Weight(2.0, "kg", "kg"),
                new Brand("", ""), "", new Model(""), "", List.of(additionalProperty), "");
    }

    private Purchase buildPurchase(final UUID customerId, final long itemId) {
        final SaleItem soldItem =
                new SaleItem(itemId, "", 2.0, 1, 3.2, 0.6, List.of(1.1), List.of(new TaxLine("", "", 2.0, 3.0, 0.2)),
                        UUID.randomUUID(), UUID.randomUUID());
        final SaleItem returnedItem =
                new SaleItem(12L, "", 3.5, 2, 2.0, 1.0, List.of(0.5), List.of(new TaxLine("", "", 2.0, 3.0, 0.2)),
                        UUID.randomUUID(), UUID.randomUUID());
        final Transaction transaction =
                new Transaction(UUID.randomUUID(), "", 3L, "", "", "ES", "Spain", "EUR", ZonedDateTime.now(),
                        ZonedDateTime.now(), "", 12.5, 12.5, 12.5, 12.5, 1.0, List.of(soldItem), List.of(returnedItem),
                        List.of(new Tender("", "", 3.0)), "");
        return new Purchase(UUID.randomUUID(), customerId, "", "", ZonedDateTime.now(), ZonedDateTime.now(),
                ZonedDateTime.now(), transaction);
    }

}
