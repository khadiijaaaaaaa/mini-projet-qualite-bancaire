package com.hendisantika.onlinebanking.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorityTest {

    @Test
    void shouldReturnAuthorityName() {
        Authority authority = new Authority("ROLE_USER");

        assertEquals("ROLE_USER", authority.getAuthority());
    }
}
