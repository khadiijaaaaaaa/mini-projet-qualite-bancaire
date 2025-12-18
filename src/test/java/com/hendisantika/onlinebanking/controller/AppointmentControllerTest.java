package com.hendisantika.onlinebanking.controller;


import com.hendisantika.onlinebanking.dto.AppointmentForm;
import com.hendisantika.onlinebanking.entity.Appointment;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.AppointmentService;
import com.hendisantika.onlinebanking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;
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

        // CORRECTION : On vérifie que c'est bien le DTO (AppointmentForm) qui est ajouté au modèle
        verify(model).addAttribute(eq("appointment"), any(AppointmentForm.class));
        verify(model).addAttribute(eq("dateString"), eq(""));
    }

    // --- TEST 2 : Soumission du formulaire (POST) ---
    @Test
    void testCreateAppointment_Post() throws ParseException {
        // 1. Arrange
        String dateString = "2025-05-20 10:30";

        // CORRECTION : On prépare un DTO, pas une Entité
        AppointmentForm form = new AppointmentForm();
        form.setLocation("Agence Paris");
        form.setDescription("Ouverture Compte");

        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);

        when(principal.getName()).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(mockUser);

        // 2. Act
        // On appelle la méthode avec le DTO
        String viewName = appointmentController.createAppointmentPost(form, dateString, model, principal);

        // 3. Assert
        assertEquals("redirect:/userFront", viewName);

        // CORRECTION MAJEURE : Capturer l'objet Appointment créé à l'intérieur du contrôleur
        // C'est le seul moyen de vérifier que le mapping DTO -> Entity s'est bien passé
        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);

        // On vérifie que le service a été appelé et on capture l'argument passé
        verify(appointmentService, times(1)).createAppointment(appointmentCaptor.capture());

        // On récupère l'objet capturé pour l'analyser
        Appointment capturedAppointment = appointmentCaptor.getValue();

        // Vérifications
        assertNotNull(capturedAppointment.getDate(), "La date doit être convertie");
        assertEquals(mockUser, capturedAppointment.getUser(), "L'user doit être assigné");
        assertEquals("Agence Paris", capturedAppointment.getLocation(), "La location doit venir du DTO");
        assertEquals("Ouverture Compte", capturedAppointment.getDescription(), "La description doit venir du DTO");
        assertFalse(capturedAppointment.isConfirmed(), "Par sécurité, confirmed doit être false");
    }
}