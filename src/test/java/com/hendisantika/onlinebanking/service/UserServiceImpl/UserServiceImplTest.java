package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.RoleDao;
import com.hendisantika.onlinebanking.repository.UserDao;
import com.hendisantika.onlinebanking.security.Role;
import com.hendisantika.onlinebanking.security.UserRole;
import com.hendisantika.onlinebanking.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AccountService accountService;

    @Test
    void save_shouldCallUserDaoSave() {
        User user = new User();
        userService.save(user);
        verify(userDao, times(1)).save(user);
    }

    @Test
    void findByUsername_shouldReturnUserFromDao() {
        User u = new User();
        u.setUsername("sara");
        when(userDao.findByUsername("sara")).thenReturn(u);

        User result = userService.findByUsername("sara");

        assertEquals(u, result);
    }

    @Test
    void createUser_userAlreadyExists_shouldReturnExistingUser_andNotCreateAccounts() {
        User existing = new User();
        existing.setUsername("sara");

        User input = new User();
        input.setUsername("sara");
        input.setPassword("123");

        when(userDao.findByUsername("sara")).thenReturn(existing);

        User result = userService.createUser(input, Collections.emptySet());

        assertEquals(existing, result);

        verify(passwordEncoder, never()).encode(anyString());
        verify(roleDao, never()).save(any());
        verify(accountService, never()).createPrimaryAccount();
        verify(accountService, never()).createSavingsAccount();
        verify(userDao, never()).save(input);
    }

    @Test
    void createUser_userDoesNotExist_shouldCreateAndReturnSavedUser() {
        // Arrange user input
        User input = new User();
        input.setUsername("newUser");
        input.setPassword("plain");

        // Si ton entity User a userRoles = null par défaut, active ces 2 lignes :
        // if (input.getUserRoles() == null) input.setUserRoles(new HashSet<>());
        // sinon garde tel quel.

        when(userDao.findByUsername("newUser")).thenReturn(null);
        when(passwordEncoder.encode("plain")).thenReturn("ENC");

        PrimaryAccount pa = new PrimaryAccount();
        SavingsAccount sa = new SavingsAccount();
        when(accountService.createPrimaryAccount()).thenReturn(pa);
        when(accountService.createSavingsAccount()).thenReturn(sa);

        // UserRole + Role (minimum)
        Role role = new Role();
        UserRole ur = mock(UserRole.class);
        when(ur.getRole()).thenReturn(role);

        Set<UserRole> roles = new HashSet<>();
        roles.add(ur);

        // User sauvé
        User saved = new User();
        saved.setUsername("newUser");
        when(userDao.save(any(User.class))).thenReturn(saved);

        // Act
        User result = userService.createUser(input, roles);

        // Assert
        assertNotNull(result);
        assertEquals(saved, result);

        assertEquals("ENC", input.getPassword());
        assertSame(pa, input.getPrimaryAccount());
        assertSame(sa, input.getSavingsAccount());

        verify(roleDao, times(1)).save(role);
        verify(userDao, times(1)).save(input);
    }

    @Test
    void checkUsernameExists_shouldReturnTrueWhenUserFound() {
        when(userDao.findByUsername("sara")).thenReturn(new User());

        assertTrue(userService.checkUsernameExists("sara"));
    }

    @Test
    void checkEmailExists_shouldReturnFalseWhenNotFound() {
        when(userDao.findByEmail("x@y.com")).thenReturn(null);

        assertFalse(userService.checkEmailExists("x@y.com"));
    }

    @Test
    void findUserList_shouldReturnAllUsers() {
        when(userDao.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> result = userService.findUserList();

        assertEquals(2, result.size());
    }

    @Test
    void enableUser_shouldSetEnabledTrueAndSave() {
        User user = new User();
        user.setUsername("sara");
        user.setEnabled(false);

        when(userDao.findByUsername("sara")).thenReturn(user);

        userService.enableUser("sara");

        assertTrue(user.isEnabled());
        verify(userDao).save(user);
    }

    @Test
    void disableUser_shouldSetEnabledFalseAndSave() {
        User user = new User();
        user.setUsername("sara");
        user.setEnabled(true);

        when(userDao.findByUsername("sara")).thenReturn(user);

        userService.disableUser("sara");

        assertFalse(user.isEnabled());
        verify(userDao).save(user);
    }
}
