package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class TaskTwoTests {
    static final Logger logger = LoggerFactory.getLogger(TaskTwoTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private FileLoader fileLoader;

    @Test
    void task_two_verifier() throws InterruptedException {
        String[] transactionLines = fileLoader.loadStrings("/test_data/poiuytrewq.uiop");

        logger.info("Loaded {} transaction lines", transactionLines.length);

        // Log what's in the file
        for (int i = 0; i < transactionLines.length; i++) {
            logger.info("Line {}: {}", i, transactionLines[i]);
        }

        // Send transactions
        for (String transactionLine : transactionLines) {
            logger.info("Sending to Kafka: {}", transactionLine);
            kafkaProducer.send(transactionLine);
        }

        logger.info("----------------------------------------------------------");
        logger.info("All transactions sent. Waiting for processing...");
        logger.info("Check the logs above for transaction details!");
        logger.info("----------------------------------------------------------");

        // Wait long enough to see the transactions being processed
        Thread.sleep(10000);

        logger.info("Test completed - check the transaction logs above for the answer");
    }
}