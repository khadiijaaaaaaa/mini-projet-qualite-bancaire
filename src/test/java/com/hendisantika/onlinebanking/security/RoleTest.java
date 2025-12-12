package com.hendisantika.onlinebanking.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

class RoleTest {

    @Test
    void testRole() {
        Role role = new Role();
        role.setRoleId(1);
        role.setName("ROLE_USER");

        Set<UserRole> userRoles = new HashSet<>();
        role.setUserRoles(userRoles);

        assertEquals(1, role.getRoleId());
        assertEquals("ROLE_USER", role.getName());
        assertEquals(userRoles, role.getUserRoles());
    }
}