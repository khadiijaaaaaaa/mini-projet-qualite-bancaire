package com.hendisantika.onlinebanking.dto;

public class AppointmentForm {
    private String location;
    private String description;

    // Constructeur vide par défaut
    public AppointmentForm() {
        // Requis par Spring MVC pour l'instanciation du DTO lors du binding
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
