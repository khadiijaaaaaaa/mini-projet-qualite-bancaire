package com.hendisantika.onlinebanking.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrimaryAccountTest {

    @Test
    void shouldStoreAndReturnFields() {
        PrimaryAccount account = new PrimaryAccount();

        account.setId(1L);
        account.setAccountNumber(123456);
        account.setAccountBalance(new BigDecimal("2500.75"));

        PrimaryTransaction tx = new PrimaryTransaction();
        account.setPrimaryTransactionList(List.of(tx));

        assertAll(
                () -> assertEquals(1L, account.getId()),
                () -> assertEquals(123456, account.getAccountNumber()),
                () -> assertEquals(new BigDecimal("2500.75"), account.getAccountBalance()),
                () -> assertNotNull(account.getPrimaryTransactionList()),
                () -> assertEquals(1, account.getPrimaryTransactionList().size())
        );
    }
}
