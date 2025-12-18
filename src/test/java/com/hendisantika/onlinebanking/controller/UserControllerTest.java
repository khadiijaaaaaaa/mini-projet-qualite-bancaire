package com.hendisantika.onlinebanking.controller;


import com.hendisantika.onlinebanking.dto.UserProfileForm;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    // --- TEST 1 : Affichage du profil (GET /profile) ---
    @Test
    void testProfile_Get() {
        // 1. Arrange
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setEmail("test@test.com");

        when(principal.getName()).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(mockUser);

        // 2. Act
        String viewName = userController.profile(principal, model);

        // 3. Assert
        assertEquals("profile", viewName);
        verify(userService, times(1)).findByUsername(username);
        verify(model, times(1)).addAttribute("user", mockUser);
    }

    // --- TEST 2 : Mise à jour du profil (POST /profile) ---
    @Test
    void testProfile_Post() {
        // 1. Arrange
        String username = "testUser";

        // CORRECTION : On utilise le DTO pour simuler les données du formulaire
        UserProfileForm form = new UserProfileForm();
        form.setUsername(username);
        form.setFirstName("NouveauPrenom");
        form.setLastName("NouveauNom");
        form.setEmail("nouveau@email.com");
        form.setPhone("0600000000");

        // L'utilisateur existant en base (les anciennes données)
        User existingUserInDb = new User();
        existingUserInDb.setUsername(username);
        existingUserInDb.setFirstName("VieuxPrenom");

        // Quand le contrôleur cherche l'user, il trouve celui en base
        when(userService.findByUsername(username)).thenReturn(existingUserInDb);

        // 2. Act
        // On passe le DTO au lieu de l'entité User
        String viewName = userController.profilePost(form, model);

        // 3. Assert
        assertEquals("profile", viewName);

        // Vérification : L'objet existant (récupéré via le mock) doit avoir été modifié par les setters
        assertEquals("NouveauPrenom", existingUserInDb.getFirstName());
        assertEquals("nouveau@email.com", existingUserInDb.getEmail());
        assertEquals("NouveauNom", existingUserInDb.getLastName());

        // Vérifie que le service a bien sauvegardé les changements sur l'entité
        verify(userService, times(1)).saveUser(existingUserInDb);
        verify(model, times(1)).addAttribute("user", existingUserInDb);
    }
}