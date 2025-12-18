package com.hendisantika.onlinebanking.resource;

import com.hendisantika.onlinebanking.entity.Appointment;
import com.hendisantika.onlinebanking.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : online-banking
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 09/08/18
 * Time: 04.31
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/api/appointment")
@PreAuthorize("hasRole('ADMIN')")
public class AppointmentResource {

    @Autowired
    private AppointmentService appointmentService;

    // CORRECTION : @GetMapping explicite au lieu de @RequestMapping implicite
    @GetMapping("/all")
    public List<Appointment> findAppointmentList() {
        return appointmentService.findAll();
    }

    // CORRECTION : @GetMapping pour expliciter que l'action se fait via URL
    @GetMapping("/{id}/confirm")
    public void confirmAppointment(@PathVariable("id") Long id) {
        appointmentService.confirmAppointment(id);
    }
}