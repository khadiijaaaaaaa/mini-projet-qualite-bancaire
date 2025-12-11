package com.hendisantika.onlinebanking.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;


import com.hendisantika.onlinebanking.entity.Appointment;
import com.hendisantika.onlinebanking.repository.AppointmentDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Mock
    private AppointmentDao appointmentDao;

    // --- TEST 1 : Créer un rendez-vous ---
    @Test
    void testCreateAppointment() {
        // Arrange
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        // Simulation : quand on sauvegarde, on retourne l'objet lui-même
        when(appointmentDao.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        Appointment created = appointmentService.createAppointment(appointment);

        // Assert
        assertNotNull(created);
        assertEquals(1L, created.getId());
        verify(appointmentDao, times(1)).save(appointment);
    }

    // --- TEST 2 : Trouver tous les rendez-vous ---
    @Test
    void testFindAll() {
        // Arrange
        List<Appointment> list = new ArrayList<>();
        list.add(new Appointment());
        list.add(new Appointment());

        when(appointmentDao.findAll()).thenReturn(list);

        // Act
        List<Appointment> result = appointmentService.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(appointmentDao, times(1)).findAll();
    }

    // --- TEST 3 : Trouver un rendez-vous par ID ---
    @Test
    void testFindAppointment() {
        // Arrange
        Long id = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(id);

        // Important : Le code source fait .get(), donc le DAO doit retourner un Optional
        when(appointmentDao.findById(id)).thenReturn(Optional.of(appointment));

        // Act
        Appointment result = appointmentService.findAppointment(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    // --- TEST 4 : Confirmer un rendez-vous ---
    @Test
    void testConfirmAppointment() {
        // Arrange
        Long id = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setConfirmed(false); // Il n'est pas confirmé au début

        // On doit simuler la recherche avant la confirmation
        when(appointmentDao.findById(id)).thenReturn(Optional.of(appointment));

        // Act
        appointmentService.confirmAppointment(id);

        // Assert
        // 1. Vérifie que l'état a changé en mémoire
        assertTrue(appointment.isConfirmed(), "Le rendez-vous devrait passer à 'confirmed=true'");

        // 2. Vérifie que la méthode save a été appelée pour persister le changement
        verify(appointmentDao, times(1)).save(appointment);
    }
}