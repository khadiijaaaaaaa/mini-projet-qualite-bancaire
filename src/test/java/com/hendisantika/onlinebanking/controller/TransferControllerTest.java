package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.dto.RecipientForm;
import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.Recipient;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.TransactionService;
import com.hendisantika.onlinebanking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @InjectMocks
    private TransferController transferController;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    // --- TEST 1 : GET /betweenAccounts
    @Test
    void testBetweenAccountsGet() {
        String view = transferController.betweenAccounts(model);

        assertEquals("betweenAccounts", view);
        verify(model).addAttribute("transferFrom", "");
        verify(model).addAttribute("transferTo", "");
        verify(model).addAttribute("amount", "");
    }

    // --- TEST 2 : POST /betweenAccounts
    @Test
    void testBetweenAccountsPost() throws Exception {
        String username = "testUser";
        when(principal.getName()).thenReturn(username);

        User user = new User();
        PrimaryAccount pa = new PrimaryAccount();
        SavingsAccount sa = new SavingsAccount();
        user.setPrimaryAccount(pa);
        user.setSavingsAccount(sa);

        when(userService.findByUsername(username)).thenReturn(user);

        String view = transferController.betweenAccountsPost("Primary", "Savings", "100", principal);

        assertEquals("redirect:/userFront", view);

        verify(transactionService).betweenAccountsTransfer("Primary", "Savings", "100", pa, sa);
    }

    // --- TEST 3 : GET /recipient
    @Test
    void testRecipientGet() {
        List<Recipient> list = List.of(new Recipient(), new Recipient());
        when(transactionService.findRecipientList(principal)).thenReturn(list);

        String view = transferController.recipient(model, principal);

        assertEquals("recipient", view);
        verify(model).addAttribute(eq("recipientList"), eq(list));
        // CORRECTION : On vérifie que c'est le DTO qui est passé
        verify(model).addAttribute(eq("recipient"), any(RecipientForm.class));
        verify(transactionService).findRecipientList(principal);
    }

    // --- TEST 4 : POST /recipient/save
    @Test
    void testRecipientSave() {
        String username = "testUser";
        when(principal.getName()).thenReturn(username);

        User user = new User();
        user.setUsername(username);

        when(userService.findByUsername(username)).thenReturn(user);

        // CORRECTION : On utilise le DTO pour l'envoi
        RecipientForm form = new RecipientForm();
        form.setName("R1");
        form.setAccountNumber("FR76");

        String view = transferController.recipientPost(form, principal);

        assertEquals("redirect:/transfer/recipient", view);

        // CORRECTION : On capture l'entité créée dans le controller
        ArgumentCaptor<Recipient> recipientCaptor = ArgumentCaptor.forClass(Recipient.class);
        verify(transactionService).saveRecipient(recipientCaptor.capture());

        Recipient capturedRecipient = recipientCaptor.getValue();
        assertEquals("R1", capturedRecipient.getName());
        assertEquals("FR76", capturedRecipient.getAccountNumber());
        assertEquals(user, capturedRecipient.getUser());
    }

    // --- TEST 5 : GET /recipient/edit
    @Test
    void testRecipientEdit() {
        String name = "SaraFriend";

        Recipient r = new Recipient();
        r.setName(name);
        r.setId(1L);

        List<Recipient> list = List.of(r);

        when(transactionService.findRecipientByName(name)).thenReturn(r);
        when(transactionService.findRecipientList(principal)).thenReturn(list);

        String view = transferController.recipientEdit(name, model, principal);

        assertEquals("recipient", view);
        verify(model).addAttribute("recipientList", list);
        // CORRECTION : On vérifie que le modèle reçoit bien un DTO mappé depuis l'entité
        verify(model).addAttribute(eq("recipient"), any(RecipientForm.class));

        verify(transactionService).findRecipientByName(name);
        verify(transactionService).findRecipientList(principal);
    }

    // --- TEST 6 : GET /recipient/delete
    @Test
    void testRecipientDelete() {
        String name = "ToDelete";

        List<Recipient> list = List.of(new Recipient());
        when(transactionService.findRecipientList(principal)).thenReturn(list);

        String view = transferController.recipientDelete(name, model, principal);

        assertEquals("recipient", view);

        verify(transactionService).deleteRecipientByName(name);
        verify(transactionService).findRecipientList(principal);

        // CORRECTION : DTO vide
        verify(model).addAttribute(eq("recipient"), any(RecipientForm.class));
        verify(model).addAttribute("recipientList", list);
    }

    // --- TEST 7 : GET /toSomeoneElse (Reste inchangé)
    @Test
    void testToSomeoneElseGet() {
        List<Recipient> list = List.of(new Recipient());
        when(transactionService.findRecipientList(principal)).thenReturn(list);

        String view = transferController.toSomeoneElse(model, principal);

        assertEquals("toSomeoneElse", view);
        verify(model).addAttribute("recipientList", list);
        verify(model).addAttribute("accountType", "");
    }

    // --- TEST 8 : POST /toSomeoneElse (Reste inchangé)
    @Test
    void testToSomeoneElsePost() {
        String username = "testUser";
        when(principal.getName()).thenReturn(username);

        User user = new User();
        PrimaryAccount pa = new PrimaryAccount();
        SavingsAccount sa = new SavingsAccount();
        user.setPrimaryAccount(pa);
        user.setSavingsAccount(sa);

        when(userService.findByUsername(username)).thenReturn(user);

        Recipient recipient = new Recipient();
        recipient.setName("R1");
        when(transactionService.findRecipientByName("R1")).thenReturn(recipient);

        String view = transferController.toSomeoneElsePost("R1", "Primary", "200", principal);

        assertEquals("redirect:/userFront", view);

        verify(transactionService).toSomeoneElseTransfer(recipient, "Primary", "200", pa, sa);
    }
}