package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.RoleDao;
import com.hendisantika.onlinebanking.security.Role;
import com.hendisantika.onlinebanking.security.UserRole;
import com.hendisantika.onlinebanking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @InjectMocks
    private HomeController homeController;

    @Mock
    private UserService userService;

    @Mock
    private RoleDao roleDao;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    // --- TEST 1 : "/" redirige vers "/index"
    @Test
    void testHome() {
        String view = homeController.home();
        assertEquals("redirect:/index", view);
    }

    // --- TEST 2 : "/index" retourne "index"
    @Test
    void testIndex() {
        String view = homeController.index();
        assertEquals("index", view);
    }

    // --- TEST 3 : GET /signup prépare un user vide et retourne "signup"
    @Test
    void testSignupGet() {
        String view = homeController.signup(model);

        assertEquals("signup", view);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    // --- TEST 4 : POST /signup -> user existe (email + username existent)
    @Test
    void testSignupPost_userExists_emailAndUsernameExist() {
        User user = new User();
        user.setUsername("sara");
        user.setEmail("sara@mail.com");

        when(userService.checkUserExists("sara", "sara@mail.com")).thenReturn(true);
        when(userService.checkEmailExists("sara@mail.com")).thenReturn(true);
        when(userService.checkUsernameExists("sara")).thenReturn(true);

        String view = homeController.signupPost(user, model);

        assertEquals("signup", view);
        verify(model).addAttribute("emailExists", true);
        verify(model).addAttribute("usernameExists", true);
        verify(userService, never()).createUser(any(User.class), anySet());
    }

    // --- TEST 5 : POST /signup -> user existe (seulement email existe)
    @Test
    void testSignupPost_userExists_onlyEmailExists() {
        User user = new User();
        user.setUsername("sara");
        user.setEmail("sara@mail.com");

        when(userService.checkUserExists("sara", "sara@mail.com")).thenReturn(true);
        when(userService.checkEmailExists("sara@mail.com")).thenReturn(true);
        when(userService.checkUsernameExists("sara")).thenReturn(false);

        String view = homeController.signupPost(user, model);

        assertEquals("signup", view);
        verify(model).addAttribute("emailExists", true);
        verify(model, never()).addAttribute(eq("usernameExists"), any());
        verify(userService, never()).createUser(any(User.class), anySet());
    }

    // --- TEST 6 : POST /signup -> user existe (seulement username existe)
    @Test
    void testSignupPost_userExists_onlyUsernameExists() {
        User user = new User();
        user.setUsername("sara");
        user.setEmail("sara@mail.com");

        when(userService.checkUserExists("sara", "sara@mail.com")).thenReturn(true);
        when(userService.checkEmailExists("sara@mail.com")).thenReturn(false);
        when(userService.checkUsernameExists("sara")).thenReturn(true);

        String view = homeController.signupPost(user, model);

        assertEquals("signup", view);
        verify(model, never()).addAttribute(eq("emailExists"), any());
        verify(model).addAttribute("usernameExists", true);
        verify(userService, never()).createUser(any(User.class), anySet());
    }

    // --- TEST 7 : POST /signup -> user n'existe pas => crée user + redirect "/"
    @Test
    void testSignupPost_userDoesNotExist_shouldCreateUserAndRedirect() {
        User user = new User();
        user.setUsername("newUser");
        user.setEmail("new@mail.com");

        when(userService.checkUserExists("newUser", "new@mail.com")).thenReturn(false);

        Role roleUser = new Role();
        when(roleDao.findByName("ROLE_USER")).thenReturn(roleUser);

        String view = homeController.signupPost(user, model);

        assertEquals("redirect:/", view);

        // on vérifie que createUser est bien appelée avec un Set contenant 1 UserRole
        verify(userService).createUser(eq(user), argThat((Set<UserRole> roles) -> roles != null && roles.size() == 1));
    }

    // --- TEST 8 : GET /userFront -> ajoute primary+savings au model et retourne "userFront"
    @Test
    void testUserFront() {
        when(principal.getName()).thenReturn("sara");

        User user = new User();
        PrimaryAccount pa = new PrimaryAccount();
        SavingsAccount sa = new SavingsAccount();
        user.setPrimaryAccount(pa);
        user.setSavingsAccount(sa);

        when(userService.findByUsername("sara")).thenReturn(user);

        String view = homeController.userFront(principal, model);

        assertEquals("userFront", view);
        verify(model).addAttribute("primaryAccount", pa);
        verify(model).addAttribute("savingsAccount", sa);
    }
}
