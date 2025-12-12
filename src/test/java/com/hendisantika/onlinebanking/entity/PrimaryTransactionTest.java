package com.hendisantika.onlinebanking.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PrimaryTransactionTest {

    @Test
    void shouldStoreAndReturnFields() {
        PrimaryAccount account = new PrimaryAccount();

        Date date = new Date();
        BigDecimal balance = new BigDecimal("1200.50");

        PrimaryTransaction tx = new PrimaryTransaction();
        tx.setId(1L);
        tx.setDate(date);
        tx.setDescription("Deposit");
        tx.setType("Credit");
        tx.setStatus("Completed");
        tx.setAmount(300.0);
        tx.setAvailableBalance(balance);
        tx.setPrimaryAccount(account);

        assertAll(
                () -> assertEquals(1L, tx.getId()),
                () -> assertEquals(date, tx.getDate()),
                () -> assertEquals("Deposit", tx.getDescription()),
                () -> assertEquals("Credit", tx.getType()),
                () -> assertEquals("Completed", tx.getStatus()),
                () -> assertEquals(300.0, tx.getAmount()),
                () -> assertEquals(balance, tx.getAvailableBalance()),
                () -> assertEquals(account, tx.getPrimaryAccount())
        );
    }
}
