package com.hendisantika.onlinebanking.entity;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecipientTest {

    @Test
    void testRecipient() {
        Recipient recipient = new Recipient();
        User user = new User();

        recipient.setId(5L);
        recipient.setName("Alice");
        recipient.setEmail("alice@test.com");
        recipient.setPhone("06000000");
        recipient.setAccountNumber("FR76");
        recipient.setDescription("Friend");
        recipient.setUser(user);

        assertEquals(5L, recipient.getId());
        assertEquals("Alice", recipient.getName());
        assertEquals("alice@test.com", recipient.getEmail());
        assertEquals("06000000", recipient.getPhone());
        assertEquals("FR76", recipient.getAccountNumber());
        assertEquals("Friend", recipient.getDescription());
        assertEquals(user, recipient.getUser());
    }
}