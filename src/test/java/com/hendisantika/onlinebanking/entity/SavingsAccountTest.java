package com.hendisantika.onlinebanking.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    @Test
    void shouldStoreAndReturnFields() {
        SavingsAccount account = new SavingsAccount();

        account.setId(2L);
        account.setAccountNumber(789456);
        account.setAccountBalance(new BigDecimal("3400.25"));

        SavingsTransaction tx = new SavingsTransaction();
        account.setSavingsTransactionList(List.of(tx));

        assertAll(
                () -> assertEquals(2L, account.getId()),
                () -> assertEquals(789456, account.getAccountNumber()),
                () -> assertEquals(new BigDecimal("3400.25"), account.getAccountBalance()),
                () -> assertNotNull(account.getSavingsTransactionList()),
                () -> assertEquals(1, account.getSavingsTransactionList().size())
        );
    }
}
