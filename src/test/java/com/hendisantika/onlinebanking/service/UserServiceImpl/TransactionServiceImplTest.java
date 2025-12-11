package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.*;
import com.hendisantika.onlinebanking.repository.*;
import com.hendisantika.onlinebanking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private UserService userService;

    @Mock
    private PrimaryTransactionDao primaryTransactionDao;

    @Mock
    private SavingsTransactionDao savingsTransactionDao;

    @Mock
    private PrimaryAccountDao primaryAccountDao;

    @Mock
    private SavingsAccountDao savingsAccountDao;

    @Mock
    private RecipientDao recipientDao;

    // --- TEST 1 : Récupérer la liste des transactions Primary ---
    @Test
    void testFindPrimaryTransactionList() {
        String username = "testUser";
        User user = new User();
        PrimaryAccount account = new PrimaryAccount();
        List<PrimaryTransaction> transactions = new ArrayList<>();
        transactions.add(new PrimaryTransaction());

        account.setPrimaryTransactionList(transactions);
        user.setPrimaryAccount(account);

        when(userService.findByUsername(username)).thenReturn(user);

        List<PrimaryTransaction> result = transactionService.findPrimaryTransactionList(username);

        assertEquals(1, result.size());
    }

    // --- TEST 2 : Récupérer la liste des transactions Savings ---
    @Test
    void testFindSavingsTransactionList() {
        String username = "testUser";
        User user = new User();
        SavingsAccount account = new SavingsAccount();
        List<SavingsTransaction> transactions = new ArrayList<>();
        transactions.add(new SavingsTransaction());

        account.setSavingsTransactionList(transactions);
        user.setSavingsAccount(account);

        when(userService.findByUsername(username)).thenReturn(user);

        List<SavingsTransaction> result = transactionService.findSavingsTransactionList(username);

        assertEquals(1, result.size());
    }

    // --- TEST 3 : Sauvegarder un dépôt (Juste vérifier l'appel au DAO) ---
    @Test
    void testSavePrimaryDepositTransaction() {
        PrimaryTransaction pt = new PrimaryTransaction();
        transactionService.savePrimaryDepositTransaction(pt);
        verify(primaryTransactionDao, times(1)).save(pt);
    }

    @Test
    void testSaveSavingsDepositTransaction() {
        SavingsTransaction st = new SavingsTransaction();
        transactionService.saveSavingsDepositTransaction(st);
        verify(savingsTransactionDao, times(1)).save(st);
    }

    // --- TEST 4 : Virement Interne (Primary vers Savings) ---
    @Test
    void testBetweenAccountsTransfer_PrimaryToSavings() throws Exception {
        // Arrange
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal(1000));

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal(500));

        String amount = "100";

        // Act
        transactionService.betweenAccountsTransfer("Primary", "Savings", amount, primaryAccount, savingsAccount);

        // Assert
        // 1000 - 100 = 900
        assertEquals(new BigDecimal(900), primaryAccount.getAccountBalance());
        // 500 + 100 = 600
        assertEquals(new BigDecimal(600), savingsAccount.getAccountBalance());

        verify(primaryAccountDao, times(1)).save(primaryAccount);
        verify(savingsAccountDao, times(1)).save(savingsAccount);
        verify(primaryTransactionDao, times(1)).save(any(PrimaryTransaction.class));
    }

    // --- TEST 5 : Virement Interne (Savings vers Primary) ---
    @Test
    void testBetweenAccountsTransfer_SavingsToPrimary() throws Exception {
        // Arrange
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal(1000));

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal(500));

        String amount = "50";

        // Act
        transactionService.betweenAccountsTransfer("Savings", "Primary", amount, primaryAccount, savingsAccount);

        // Assert
        // 1000 + 50 = 1050
        assertEquals(new BigDecimal(1050), primaryAccount.getAccountBalance());
        // 500 - 50 = 450
        assertEquals(new BigDecimal(450), savingsAccount.getAccountBalance());

        verify(savingsTransactionDao, times(1)).save(any(SavingsTransaction.class));
    }

    // --- TEST 6 : Virement Interne Invalide (Exception) ---
    @Test
    void testBetweenAccountsTransfer_Invalid() {
        assertThrows(Exception.class, () -> {
            transactionService.betweenAccountsTransfer("Unknown", "Unknown", "100", new PrimaryAccount(), new SavingsAccount());
        });
    }

    // --- TEST 7 : Filtrer les bénéficiaires par utilisateur ---
    @Test
    void testFindRecipientList() {
        // Arrange
        String myUsername = "moi";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(myUsername);

        User me = new User();
        me.setUsername(myUsername);

        User other = new User();
        other.setUsername("autre");

        Recipient r1 = new Recipient(); r1.setUser(me);
        Recipient r2 = new Recipient(); r2.setUser(other);

        // Le DAO renvoie tout le monde
        when(recipientDao.findAll()).thenReturn(Arrays.asList(r1, r2));

        // Act
        List<Recipient> result = transactionService.findRecipientList(principal);

        // Assert : On ne doit récupérer que r1 (celui qui m'appartient)
        assertEquals(1, result.size());
        assertEquals(me, result.get(0).getUser());
    }

    // --- TEST 8 : Virement vers un tiers (Depuis Primary) ---
    @Test
    void testToSomeoneElseTransfer_FromPrimary() {
        // Arrange
        Recipient recipient = new Recipient();
        recipient.setName("Copain");

        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal(1000));

        String amount = "200";

        // Act
        transactionService.toSomeoneElseTransfer(recipient, "Primary", amount, primaryAccount, null);

        // Assert
        assertEquals(new BigDecimal(800), primaryAccount.getAccountBalance()); // 1000 - 200
        verify(primaryAccountDao, times(1)).save(primaryAccount);
        verify(primaryTransactionDao, times(1)).save(any(PrimaryTransaction.class));
    }

    // --- TEST 9 : Virement vers un tiers (Depuis Savings) ---
    @Test
    void testToSomeoneElseTransfer_FromSavings() {
        // Arrange
        Recipient recipient = new Recipient();
        recipient.setName("Maman");

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal(500));

        String amount = "50";

        // Act
        transactionService.toSomeoneElseTransfer(recipient, "Savings", amount, null, savingsAccount);

        // Assert
        assertEquals(new BigDecimal(450), savingsAccount.getAccountBalance()); // 500 - 50
        verify(savingsAccountDao, times(1)).save(savingsAccount);
        verify(savingsTransactionDao, times(1)).save(any(SavingsTransaction.class));
    }

    // --- TEST 10 : Gestion des Recipient (Save / Find / Delete) ---
    @Test
    void testRecipientManagement() {
        // Test Save
        Recipient r = new Recipient();
        transactionService.saveRecipient(r);
        verify(recipientDao, times(1)).save(r);

        // Test Find
        String name = "Toto";
        transactionService.findRecipientByName(name);
        verify(recipientDao, times(1)).findByName(name);

        // Test Delete
        transactionService.deleteRecipientByName(name);
        verify(recipientDao, times(1)).deleteByName(name);
    }
}