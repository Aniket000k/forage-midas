package com.jpmc.midascore.foundation;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TransactionConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransaction(Transaction transaction) {
        logger.info("🎯 TRANSACTION CONSUMER ACTIVATED!");
        logger.info("💰 Received Transaction:");
        logger.info("   Sender ID: {}", transaction.getSenderId());
        logger.info("   Recipient ID: {}", transaction.getRecipientId());
        logger.info("   Amount: {}", transaction.getAmount());
        logger.info("   Full Object: {}", transaction);
        logger.info("----------------------------------------");
    }
}