package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.dto.UserSignupForm;
import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.RoleDao;
import com.hendisantika.onlinebanking.security.Role;
import com.hendisantika.onlinebanking.security.UserRole;
import com.hendisantika.onlinebanking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    // --- TEST 3 : GET /signup prépare un form vide et retourne "signup"
    @Test
    void testSignupGet() {
        String view = homeController.signup(model);

        assertEquals("signup", view);
        // CORRECTION : On vérifie que c'est le DTO qui est ajouté
        verify(model).addAttribute(eq("user"), any(UserSignupForm.class));
    }

    // --- TEST 4 : POST /signup -> user existe (email + username existent)
    @Test
    void testSignupPost_userExists_emailAndUsernameExist() {
        // CORRECTION : On utilise le DTO
        UserSignupForm form = new UserSignupForm();
        form.setUsername("sara");
        form.setEmail("sara@mail.com");

        when(userService.checkUserExists("sara", "sara@mail.com")).thenReturn(true);
        when(userService.checkEmailExists("sara@mail.com")).thenReturn(true);
        when(userService.checkUsernameExists("sara")).thenReturn(true);

        String view = homeController.signupPost(form, model);

        assertEquals("signup", view);
        verify(model).addAttribute("emailExists", true);
        verify(model).addAttribute("usernameExists", true);
        verify(userService, never()).createUser(any(User.class), anySet());
    }

    // --- TEST 5 : POST /signup -> user existe (seulement email existe)
    @Test
    void testSignupPost_userExists_onlyEmailExists() {
        UserSignupForm form = new UserSignupForm();
        form.setUsername("sara");
        form.setEmail("sara@mail.com");

        when(userService.checkUserExists("sara", "sara@mail.com")).thenReturn(true);
        when(userService.checkEmailExists("sara@mail.com")).thenReturn(true);
        when(userService.checkUsernameExists("sara")).thenReturn(false);

        String view = homeController.signupPost(form, model);

        assertEquals("signup", view);
        verify(model).addAttribute("emailExists", true);
        verify(userService, never()).createUser(any(User.class), anySet());
    }

    // --- TEST 6 : POST /signup -> user existe (seulement username existe)
    @Test
    void testSignupPost_userExists_onlyUsernameExists() {
        UserSignupForm form = new UserSignupForm();
        form.setUsername("sara");
        form.setEmail("sara@mail.com");

        when(userService.checkUserExists("sara", "sara@mail.com")).thenReturn(true);
        when(userService.checkEmailExists("sara@mail.com")).thenReturn(false);
        when(userService.checkUsernameExists("sara")).thenReturn(true);

        String view = homeController.signupPost(form, model);

        assertEquals("signup", view);
        verify(model).addAttribute("usernameExists", true);
        verify(userService, never()).createUser(any(User.class), anySet());
    }

    // --- TEST 7 : POST /signup -> user n'existe pas => crée user + redirect "/"
    @Test
    void testSignupPost_userDoesNotExist_shouldCreateUserAndRedirect() {
        // CORRECTION : Préparation du DTO
        UserSignupForm form = new UserSignupForm();
        form.setUsername("newUser");
        form.setEmail("new@mail.com");
        form.setPassword("password123");

        when(userService.checkUserExists("newUser", "new@mail.com")).thenReturn(false);

        Role roleUser = new Role();
        when(roleDao.findByName("ROLE_USER")).thenReturn(roleUser);

        // Act
        String view = homeController.signupPost(form, model);

        // Assert
        assertEquals("redirect:/", view);

        // CORRECTION : Capture de l'entité User créée dans le controller
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Vérification que createUser a été appelé avec un User qui a les infos du DTO
        verify(userService).createUser(userCaptor.capture(), argThat((Set<UserRole> roles) -> roles != null && roles.size() == 1));

        User capturedUser = userCaptor.getValue();
        assertEquals("newUser", capturedUser.getUsername());
        assertEquals("new@mail.com", capturedUser.getEmail());
    }

    // --- TEST 8 : GET /userFront -> (Reste inchangé)
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