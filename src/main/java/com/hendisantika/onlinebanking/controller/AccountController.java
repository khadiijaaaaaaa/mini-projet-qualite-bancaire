package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.SavingsTransaction;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.AccountService;
import com.hendisantika.onlinebanking.service.TransactionService;
import com.hendisantika.onlinebanking.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public AccountController(
            @Qualifier("userServiceImpl") UserService userService,
            AccountService accountService,
            TransactionService transactionService
    ) {
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    // CORRECTION : @GetMapping explicite
    @GetMapping("/primaryAccount")
    public String primaryAccount(Model model, Principal principal) {
        List<PrimaryTransaction> primaryTransactionList =
                transactionService.findPrimaryTransactionList(principal.getName());

        User user = userService.findByUsername(principal.getName());
        PrimaryAccount primaryAccount = user.getPrimaryAccount();

        model.addAttribute("primaryAccount", primaryAccount);
        model.addAttribute("primaryTransactionList", primaryTransactionList);

        return "primaryAccount";
    }

    // CORRECTION : @GetMapping explicite
    @GetMapping("/savingsAccount")
    public String savingsAccount(Model model, Principal principal) {
        List<SavingsTransaction> savingsTransactionList =
                transactionService.findSavingsTransactionList(principal.getName());

        User user = userService.findByUsername(principal.getName());
        SavingsAccount savingsAccount = user.getSavingsAccount();

        model.addAttribute("savingsAccount", savingsAccount);
        model.addAttribute("savingsTransactionList", savingsTransactionList);

        return "savingsAccount";
    }

    // CORRECTION : @GetMapping moderne
    @GetMapping("/deposit")
    public String deposit(Model model) {
        model.addAttribute("accountType", "");
        model.addAttribute("amount", "");
        return "deposit";
    }

    // CORRECTION : @PostMapping moderne
    @PostMapping("/deposit")
    public String depositPOST(@ModelAttribute("amount") String amount,
                              @ModelAttribute("accountType") String accountType,
                              Principal principal) {
        accountService.deposit(accountType, Double.parseDouble(amount), principal);
        return "redirect:/userFront";
    }

    // CORRECTION : @GetMapping moderne
    @GetMapping("/withdraw")
    public String withdraw(Model model) {
        model.addAttribute("accountType", "");
        model.addAttribute("amount", "");
        return "withdraw";
    }

    // CORRECTION : @PostMapping moderne
    @PostMapping("/withdraw")
    public String withdrawPOST(@ModelAttribute("amount") String amount,
                               @ModelAttribute("accountType") String accountType,
                               Principal principal) {
        accountService.withdraw(accountType, Double.parseDouble(amount), principal);
        return "redirect:/userFront";
    }
}