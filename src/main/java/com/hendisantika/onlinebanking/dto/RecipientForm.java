package com.hendisantika.onlinebanking.dto;

// Hérite de BaseContactForm pour récupérer Email et Phone sans duplication
public class RecipientForm extends BaseContactForm {
    private Long id;
    private String name;
    private String accountNumber;
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}