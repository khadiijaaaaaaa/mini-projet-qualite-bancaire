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
class UserSecurityServiceImplTest {

    @InjectMocks
    private UserSecurityServiceImpl userSecurityService;

    @Mock
    private UserDao userDao;

    @Test
    void loadUserByUsername_userExists_returnsUser() {
        // Arrange
        User user = new User();
        user.setUsername("sara");

        when(userDao.findByUsername("sara")).thenReturn(user);

        // Act
        UserDetails result = userSecurityService.loadUserByUsername("sara");

        // Assert
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        // Arrange
        when(userDao.findByUsername("unknown")).thenReturn(null);

        // Act + Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userSecurityService.loadUserByUsername("unknown")
        );

        assertTrue(exception.getMessage().contains("unknown"));
    }
}
