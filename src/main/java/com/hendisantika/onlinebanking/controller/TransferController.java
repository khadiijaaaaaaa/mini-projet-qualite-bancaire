package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.dto.RecipientForm;
import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.Recipient;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.TransactionService;
import com.hendisantika.onlinebanking.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/transfer")
public class TransferController {

    private final TransactionService transactionService;
    private final UserService userService;

    // CORRECTION (S1192) : Constantes pour éviter la duplication de String literals
    private static final String RECIPIENT = "recipient";
    private static final String RECIPIENT_LIST = "recipientList";

    public TransferController(
            TransactionService transactionService,
            @Qualifier("userServiceImpl") UserService userService
    ) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    // CORRECTION (S3752) : @GetMapping explicite
    @GetMapping("/betweenAccounts")
    public String betweenAccounts(Model model) {
        model.addAttribute("transferFrom", "");
        model.addAttribute("transferTo", "");
        model.addAttribute("amount", "");
        return "betweenAccounts";
    }

    // CORRECTION (S3752) : @PostMapping explicite
    @PostMapping("/betweenAccounts")
    public String betweenAccountsPost(
            @ModelAttribute("transferFrom") String transferFrom,
            @ModelAttribute("transferTo") String transferTo,
            @ModelAttribute("amount") String amount,
            Principal principal
    ) throws Exception {

        User user = userService.findByUsername(principal.getName());
        PrimaryAccount primaryAccount = user.getPrimaryAccount();
        SavingsAccount savingsAccount = user.getSavingsAccount();

        transactionService.betweenAccountsTransfer(
                transferFrom,
                transferTo,
                amount,
                primaryAccount,
                savingsAccount
        );

        return "redirect:/userFront";
    }

    @GetMapping("/recipient")
    public String recipient(Model model, Principal principal) {
        List<Recipient> recipientList = transactionService.findRecipientList(principal);

        // Utilisation des constantes
        model.addAttribute(RECIPIENT_LIST, recipientList);
        model.addAttribute(RECIPIENT, new RecipientForm());

        return RECIPIENT;
    }

    @PostMapping("/recipient/save")
    public String recipientPost(@ModelAttribute(RECIPIENT) RecipientForm recipientForm, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        Recipient recipient = new Recipient();
        recipient.setId(recipientForm.getId());
        recipient.setName(recipientForm.getName());
        recipient.setEmail(recipientForm.getEmail());
        recipient.setPhone(recipientForm.getPhone());
        recipient.setAccountNumber(recipientForm.getAccountNumber());
        recipient.setDescription(recipientForm.getDescription());
        recipient.setUser(user);

        transactionService.saveRecipient(recipient);

        return "redirect:/transfer/recipient";
    }

    @GetMapping("/recipient/edit")
    public String recipientEdit(
            @RequestParam("recipientName") String recipientName,
            Model model,
            Principal principal
    ) {
        Recipient recipient = transactionService.findRecipientByName(recipientName);
        List<Recipient> recipientList = transactionService.findRecipientList(principal);

        RecipientForm form = new RecipientForm();
        form.setId(recipient.getId());
        form.setName(recipient.getName());
        form.setEmail(recipient.getEmail());
        form.setPhone(recipient.getPhone());
        form.setAccountNumber(recipient.getAccountNumber());
        form.setDescription(recipient.getDescription());

        // Utilisation des constantes
        model.addAttribute(RECIPIENT_LIST, recipientList);
        model.addAttribute(RECIPIENT, form);

        return RECIPIENT;
    }

    @GetMapping("/recipient/delete")
    @Transactional
    public String recipientDelete(
            @RequestParam("recipientName") String recipientName,
            Model model,
            Principal principal
    ) {
        transactionService.deleteRecipientByName(recipientName);

        List<Recipient> recipientList = transactionService.findRecipientList(principal);

        model.addAttribute(RECIPIENT_LIST, recipientList);
        model.addAttribute(RECIPIENT, new RecipientForm());

        return RECIPIENT;
    }

    @GetMapping("/toSomeoneElse")
    public String toSomeoneElse(Model model, Principal principal) {
        model.addAttribute(RECIPIENT_LIST, transactionService.findRecipientList(principal));
        model.addAttribute("accountType", "");
        return "toSomeoneElse";
    }

    @PostMapping("/toSomeoneElse")
    public String toSomeoneElsePost(
            @ModelAttribute("recipientName") String recipientName,
            @ModelAttribute("accountType") String accountType,
            @ModelAttribute("amount") String amount,
            Principal principal
    ) {
        User user = userService.findByUsername(principal.getName());
        Recipient recipient = transactionService.findRecipientByName(recipientName);

        transactionService.toSomeoneElseTransfer(
                recipient,
                accountType,
                amount,
                user.getPrimaryAccount(),
                user.getSavingsAccount()
        );

        return "redirect:/userFront";
    }
}