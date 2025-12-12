package com.hendisantika.onlinebanking.security;

import static org.junit.jupiter.api.Assertions.*;


import com.hendisantika.onlinebanking.entity.User;
import org.junit.jupiter.api.Test;

class UserRoleTest {

    @Test
    void testUserRoleConstructorsAndGetters() {
        // Test constructeur vide
        UserRole urEmpty = new UserRole();
        urEmpty.setUserRoleId(100L);
        assertEquals(100L, urEmpty.getUserRoleId());

        // Test constructeur complet
        User user = new User();
        Role role = new Role();
        UserRole urFull = new UserRole(user, role);

        assertEquals(user, urFull.getUser());
        assertEquals(role, urFull.getRole());

        // Test Setters
        User newUser = new User();
        Role newRole = new Role();
        urFull.setUser(newUser);
        urFull.setRole(newRole);

        assertEquals(newUser, urFull.getUser());
        assertEquals(newRole, urFull.getRole());
    }
}
