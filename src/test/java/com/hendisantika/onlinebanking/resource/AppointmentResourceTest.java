package com.hendisantika.onlinebanking.resource;

import com.hendisantika.onlinebanking.entity.Appointment;
import com.hendisantika.onlinebanking.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentResourceTest {

    @InjectMocks
    private AppointmentResource appointmentResource;

    @Mock
    private AppointmentService appointmentService;

    // --- TEST 1 : GET /api/appointment/all
    @Test
    void findAppointmentList_shouldReturnAllAppointments() {
        List<Appointment> appointments = List.of(new Appointment(), new Appointment());
        when(appointmentService.findAll()).thenReturn(appointments);

        List<Appointment> result = appointmentResource.findAppointmentList();

        assertEquals(2, result.size());
        verify(appointmentService).findAll();
    }

    // --- TEST 2 : /{id}/confirm
    @Test
    void confirmAppointment_shouldCallService() {
        Long id = 10L;

        appointmentResource.confirmAppointment(id);

        verify(appointmentService).confirmAppointment(id);
    }
}
