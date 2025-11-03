package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class UserRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private float balance;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<TransactionRecord> sentTransactions;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private List<TransactionRecord> receivedTransactions;

    public UserRecord() {}

    public UserRecord(String username, float balance) {
        this.username = username;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "UserRecord{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                '}';
    }
}