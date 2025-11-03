package com.jpmc.midascore.foundation;

import com.jpmc.midascore.entity.Incentive;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private final String topic = "transactions";
    private final String incentiveApiUrl = "http://localhost:8080/incentive";

    @KafkaListener(topics = "${general.kafka-topic:transactions}", groupId = "${spring.kafka.consumer.group-id:midas-group}")
    public void listen(Transaction transaction) {
        logger.info("🎯 Received Transaction: {}", transaction);

        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());

        if (senderOpt.isPresent() && recipientOpt.isPresent()) {
            UserRecord sender = senderOpt.get();
            UserRecord recipient = recipientOpt.get();
            float amount = transaction.getAmount();

            if (sender.getBalance() >= amount) {
                try {
                    // ✅ Post transaction to incentive API
                    Incentive incentive = restTemplate.postForObject(incentiveApiUrl, transaction, Incentive.class);
                    float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0.0f;

                    // ✅ Update balances (add incentive to recipient)
                    sender.setBalance(sender.getBalance() - amount);
                    recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

                    userRepository.save(sender);
                    userRepository.save(recipient);

                    // ✅ Save transaction record with incentive
                    TransactionRecord record = new TransactionRecord(sender, recipient, amount, incentiveAmount);
                    transactionRecordRepository.save(record);

                    logger.info("✅ SUCCESS: Transaction saved | Incentive: {} | Sender: {} | Recipient: {}",
                            incentiveAmount, sender.getUsername(), recipient.getUsername());
                } catch (Exception e) {
                    logger.error("❌ ERROR calling Incentive API: {}", e.getMessage());
                }

            } else {
                logger.warn("⚠️ SKIPPED: Insufficient balance for sender {}", sender.getUsername());
            }
        } else {
            logger.warn("⚠️ SKIPPED: Invalid user ID in transaction {}", transaction);
        }
    }

    public String getTopic() {
        return topic;
    }
}
