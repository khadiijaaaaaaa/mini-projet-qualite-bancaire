package com.hendisantika.onlinebanking.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.hendisantika.onlinebanking.entity.Appointment;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.AppointmentService;
import com.hendisantika.onlinebanking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @InjectMocks
    private AppointmentController appointmentController;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    // --- TEST 1 : Affichage du formulaire (GET) ---
    @Test
    void testCreateAppointment_Get() {
        // Act
        String viewName = appointmentController.createAppointment(model);

        // Assert
        assertEquals("appointment", viewName, "Doit retourner la vue 'appointment'");

        // Vérifie qu'on prépare bien les attributs pour la page HTML
        verify(model).addAttribute(eq("appointment"), any(Appointment.class));
        verify(model).addAttribute(eq("dateString"), eq(""));
    }

    // --- TEST 2 : Soumission du formulaire (POST) ---
    @Test
    void testCreateAppointment_Post() throws ParseException {
        // 1. Arrange
        // Attention : Le format dans le controller est "yyyy-MM-dd hh:mm"
        String dateString = "2025-05-20 10:30";
        Appointment appointment = new Appointment();

        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);

        // Mock du système d'authentification et du service user
        when(principal.getName()).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(mockUser);

        // 2. Act
        String viewName = appointmentController.createAppointmentPost(appointment, dateString, model, principal);

        // 3. Assert
        assertEquals("redirect:/userFront", viewName);

        // Vérification Clé 1 : Est-ce que le String a été parsé en vraie Date Java ?
        assertNotNull(appointment.getDate(), "La date doit être convertie du String vers l'objet Date");

        // Vérification Clé 2 : Est-ce que le rendez-vous est bien lié à l'utilisateur ?
        assertEquals(mockUser, appointment.getUser(), "L'utilisateur connecté doit être assigné au rendez-vous");

        // Vérification Clé 3 : Est-ce qu'on a bien appelé le service pour sauvegarder ?
        verify(appointmentService, times(1)).createAppointment(appointment);
    }
}