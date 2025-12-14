package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.entity.Appointment;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.AppointmentService;
import com.hendisantika.onlinebanking.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    public AppointmentController(
            AppointmentService appointmentService,
            @Qualifier("userServiceImpl") UserService userService
    ) {
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createAppointment(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("dateString", "");
        return "appointment";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createAppointmentPost(
            @ModelAttribute("appointment") Appointment appointment,
            @ModelAttribute("dateString") String date,
            Model model,
            Principal principal
    ) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date parsedDate = format.parse(date);
        appointment.setDate(parsedDate);

        User user = userService.findByUsername(principal.getName());
        appointment.setUser(user);

        appointmentService.createAppointment(appointment);

        return "redirect:/userFront";
    }
}
