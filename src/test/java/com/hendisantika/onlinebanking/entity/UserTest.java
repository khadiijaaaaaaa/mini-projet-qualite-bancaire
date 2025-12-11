package com.hendisantika.onlinebanking.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.hendisantika.onlinebanking.security.Role;
import com.hendisantika.onlinebanking.security.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("toto");
        user.setPassword("pass");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@doe.com");
        user.setPhone("123456789");
        user.setEnabled(true);

        assertEquals(1L, user.getUserId());
        assertEquals("toto", user.getUsername());
        assertEquals("pass", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john@doe.com", user.getEmail());
        assertEquals("123456789", user.getPhone());
        assertTrue(user.isEnabled());
    }

    @Test
    void testRelationships() {
        User user = new User();

        // Test Account relationships
        PrimaryAccount pa = new PrimaryAccount();
        SavingsAccount sa = new SavingsAccount();
        user.setPrimaryAccount(pa);
        user.setSavingsAccount(sa);
        assertEquals(pa, user.getPrimaryAccount());
        assertEquals(sa, user.getSavingsAccount());

        // Test Lists
        List<Appointment> appointments = new ArrayList<>();
        List<Recipient> recipients = new ArrayList<>();
        user.setAppointmentList(appointments);
        user.setRecipientList(recipients);
        assertEquals(appointments, user.getAppointmentList());
        assertEquals(recipients, user.getRecipientList());
    }

    @Test
    void testGetAuthorities() {
        // Test de la logique de transformation Role -> Authority
        User user = new User();
        Set<UserRole> userRoles = new HashSet<>();

        Role role = new Role();
        role.setName("ROLE_ADMIN");

        UserRole userRole = new UserRole(user, role);
        userRoles.add(userRole);

        user.setUserRoles(userRoles);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        // Vérifie que le nom du rôle est bien présent dans les autorités
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testUserDetailsMethods() {
        // Ces méthodes renvoient true par défaut ("hardcodé"), on vérifie ça
        User user = new User();
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testToString() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("test");
        assertNotNull(user.toString());
    }
}