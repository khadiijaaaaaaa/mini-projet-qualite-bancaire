package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.entity.*;
import com.hendisantika.onlinebanking.service.AccountService;
import com.hendisantika.onlinebanking.service.TransactionService;
import com.hendisantika.onlinebanking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private UserService userService;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Test
    void testPrimaryAccount() {
        String username = "testUser";

        when(principal.getName()).thenReturn(username);

        List<PrimaryTransaction> txs = List.of(new PrimaryTransaction());
        when(transactionService.findPrimaryTransactionList(username)).thenReturn(txs);

        User user = new User();
        PrimaryAccount pa = new PrimaryAccount();
        user.setPrimaryAccount(pa);

        when(userService.findByUsername(username)).thenReturn(user);

        String view = accountController.primaryAccount(model, principal);

        assertEquals("primaryAccount", view);

        verify(transactionService).findPrimaryTransactionList(username);
        verify(userService).findByUsername(username);
        verify(model).addAttribute(eq("primaryAccount"), eq(pa));
        verify(model).addAttribute(eq("primaryTransactionList"), eq(txs));
    }

    @Test
    void testSavingsAccount() {
        String username = "testUser";

        when(principal.getName()).thenReturn(username);

        List<SavingsTransaction> txs = List.of(new SavingsTransaction());
        when(transactionService.findSavingsTransactionList(username)).thenReturn(txs);

        User user = new User();
        SavingsAccount sa = new SavingsAccount();
        user.setSavingsAccount(sa);

        when(userService.findByUsername(username)).thenReturn(user);

        String view = accountController.savingsAccount(model, principal);

        assertEquals("savingsAccount", view);

        verify(transactionService).findSavingsTransactionList(username);
        verify(userService).findByUsername(username);
        verify(model).addAttribute(eq("savingsAccount"), eq(sa));
        verify(model).addAttribute(eq("savingsTransactionList"), eq(txs));
    }

    @Test
    void testDepositGet() {
        String view = accountController.deposit(model);

        assertEquals("deposit", view);
        verify(model).addAttribute("accountType", "");
        verify(model).addAttribute("amount", "");
    }

    @Test
    void testDepositPost() {
        String amount = "150";
        String accountType = "Primary";

        String view = accountController.depositPOST(amount, accountType, principal);

        assertEquals("redirect:/userFront", view);
        verify(accountService).deposit(eq("Primary"), eq(150.0), eq(principal));
    }

    @Test
    void testWithdrawGet() {
        String view = accountController.withdraw(model);

        assertEquals("withdraw", view);
        verify(model).addAttribute("accountType", "");
        verify(model).addAttribute("amount", "");
    }

    @Test
    void testWithdrawPost() {
        String amount = "40";
        String accountType = "Savings";

        String view = accountController.withdrawPOST(amount, accountType, principal);

        assertEquals("redirect:/userFront", view);
        verify(accountService).withdraw(eq("Savings"), eq(40.0), eq(principal));
    }
}
