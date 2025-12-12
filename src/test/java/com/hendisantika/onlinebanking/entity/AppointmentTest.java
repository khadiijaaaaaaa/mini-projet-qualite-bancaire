package com.hendisantika.onlinebanking.entity;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import java.util.Date;

class AppointmentTest {

    @Test
    void testAppointment() {
        Appointment appointment = new Appointment();
        Date date = new Date();
        User user = new User();

        appointment.setId(10L);
        appointment.setDate(date);
        appointment.setLocation("Paris");
        appointment.setDescription("Meeting");
        appointment.setConfirmed(true);
        appointment.setUser(user);

        assertEquals(10L, appointment.getId());
        assertEquals(date, appointment.getDate());
        assertEquals("Paris", appointment.getLocation());
        assertEquals("Meeting", appointment.getDescription());
        assertTrue(appointment.isConfirmed());
        assertEquals(user, appointment.getUser());

        assertNotNull(appointment.toString());
    }
}