package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.dto.AppointmentForm;
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
        // CORRECTION SÉCURITÉ : On envoie un DTO vide au lieu de l'Entité persistante
        AppointmentForm appointmentForm = new AppointmentForm();
        model.addAttribute("appointment", appointmentForm);
        model.addAttribute("dateString", "");
        return "appointment";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createAppointmentPost(
            // CORRECTION SÉCURITÉ : On bind les données sur le DTO, pas sur l'Entité
            @ModelAttribute("appointment") AppointmentForm appointmentForm,
            @ModelAttribute("dateString") String date,
            Model model,
            Principal principal
    ) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date parsedDate = format.parse(date);

        User user = userService.findByUsername(principal.getName());

        // MAPPING MANUEL : On transfère les données du DTO vers une nouvelle Entité sécurisée
        Appointment appointment = new Appointment();
        appointment.setLocation(appointmentForm.getLocation());
        appointment.setDescription(appointmentForm.getDescription());
        appointment.setDate(parsedDate);
        appointment.setUser(user);
        // Par sécurité, on s'assure que le rendez-vous n'est pas confirmé par défaut
        appointment.setConfirmed(false);

        appointmentService.createAppointment(appointment);

        return "redirect:/userFront";
    }
}