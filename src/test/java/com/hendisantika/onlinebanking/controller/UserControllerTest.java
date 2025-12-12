package com.hendisantika.onlinebanking.controller;


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
        // 1. Arrange (Préparation)
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setEmail("test@test.com");

        // On simule l'utilisateur connecté
        when(principal.getName()).thenReturn(username);
        // On simule la réponse du service
        when(userService.findByUsername(username)).thenReturn(mockUser);

        // 2. Act (Action)
        String viewName = userController.profile(principal, model);

        // 3. Assert (Vérification)
        assertEquals("profile", viewName, "Le contrôleur doit retourner le nom de la vue 'profile'");

        // Vérifie qu'on a bien cherché l'user et qu'on l'a ajouté au modèle pour l'affichage HTML
        verify(userService, times(1)).findByUsername(username);
        verify(model, times(1)).addAttribute("user", mockUser);
    }

    // --- TEST 2 : Mise à jour du profil (POST /profile) ---
    @Test
    void testProfile_Post() {
        // 1. Arrange
        String username = "testUser";

        // L'utilisateur envoyé par le formulaire (les nouvelles données)
        User newUserForm = new User();
        newUserForm.setUsername(username);
        newUserForm.setFirstName("NouveauPrenom");
        newUserForm.setLastName("NouveauNom");
        newUserForm.setEmail("nouveau@email.com");
        newUserForm.setPhone("0600000000");

        // L'utilisateur existant en base (les anciennes données)
        User existingUserInDb = new User();
        existingUserInDb.setUsername(username);
        existingUserInDb.setFirstName("VieuxPrenom");

        // Quand le contrôleur cherche l'user, il trouve celui en base
        when(userService.findByUsername(username)).thenReturn(existingUserInDb);

        // 2. Act
        String viewName = userController.profilePost(newUserForm, model);

        // 3. Assert
        assertEquals("profile", viewName);

        // Vérification cruciale : Est-ce que l'objet existant a bien été mis à jour avec les nouvelles valeurs ?
        assertEquals("NouveauPrenom", existingUserInDb.getFirstName());
        assertEquals("nouveau@email.com", existingUserInDb.getEmail());

        // Vérifie que le service a bien sauvegardé les changements
        verify(userService, times(1)).saveUser(existingUserInDb);
        verify(model, times(1)).addAttribute("user", existingUserInDb);
    }
}