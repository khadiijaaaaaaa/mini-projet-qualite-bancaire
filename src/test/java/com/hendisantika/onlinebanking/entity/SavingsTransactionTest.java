package com.hendisantika.onlinebanking.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SavingsTransactionTest {

    @Test
    void shouldStoreAndReturnFields() {
        SavingsAccount account = new SavingsAccount();

        Date date = new Date();
        BigDecimal balance = new BigDecimal("1800.75");

        SavingsTransaction tx = new SavingsTransaction();
        tx.setId(3L);
        tx.setDate(date);
        tx.setDescription("Withdraw");
        tx.setType("Debit");
        tx.setStatus("Completed");
        tx.setAmount(250.0);
        tx.setAvailableBalance(balance);
        tx.setSavingsAccount(account);

        assertAll(
                () -> assertEquals(3L, tx.getId()),
                () -> assertEquals(date, tx.getDate()),
                () -> assertEquals("Withdraw", tx.getDescription()),
                () -> assertEquals("Debit", tx.getType()),
                () -> assertEquals("Completed", tx.getStatus()),
                () -> assertEquals(250.0, tx.getAmount()),
                () -> assertEquals(balance, tx.getAvailableBalance()),
                () -> assertEquals(account, tx.getSavingsAccount())
        );
    }
}
