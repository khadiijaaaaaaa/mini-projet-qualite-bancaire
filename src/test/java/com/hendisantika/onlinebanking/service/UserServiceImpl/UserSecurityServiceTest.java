package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSecurityServiceTest {

    @InjectMocks
    private UserSecurityService userSecurityService;

    @Mock
    private UserDao userDao;

    @Test
    void loadUserByUsername_userExists_returnsUserDetails() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");

        when(userDao.findByUsername("testUser")).thenReturn(user);

        // Act
        UserDetails result = userSecurityService.loadUserByUsername("testUser");

        // Assert
        assertNotNull(result);
        assertEquals(user, result); // car la méthode retourne directement l'entité User
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        // Arrange
        when(userDao.findByUsername("unknown")).thenReturn(null);

        // Act + Assert
        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> userSecurityService.loadUserByUsername("unknown")
        );

        assertTrue(ex.getMessage().contains("unknown"));
    }
}
