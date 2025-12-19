package com.hendisantika.onlinebanking.dto;

public class UserSignupForm extends UserBaseForm {

    private String password; // Seul champ spécifique à l'inscription

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}