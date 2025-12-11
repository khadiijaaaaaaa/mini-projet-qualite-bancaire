package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.SavingsTransaction;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.PrimaryAccountDao;
import com.hendisantika.onlinebanking.repository.SavingsAccountDao;
import com.hendisantika.onlinebanking.service.TransactionService;
import com.hendisantika.onlinebanking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private PrimaryAccountDao primaryAccountDao;

    @Mock
    private SavingsAccountDao savingsAccountDao;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    // --- TEST 1 : Création Primary ---
    @Test
    void testCreatePrimaryAccount() {
        PrimaryAccount mockAccount = new PrimaryAccount();
        mockAccount.setAccountNumber(12345);
        when(primaryAccountDao.findByAccountNumber(anyInt())).thenReturn(mockAccount);

        PrimaryAccount created = accountService.createPrimaryAccount();

        assertEquals(12345, created.getAccountNumber());
        verify(primaryAccountDao, times(1)).save(any(PrimaryAccount.class));
    }

    // --- TEST 2 : Création Savings ---
    @Test
    void testCreateSavingsAccount() {
        SavingsAccount mockAccount = new SavingsAccount();
        mockAccount.setAccountNumber(67890);
        when(savingsAccountDao.findByAccountNumber(anyInt())).thenReturn(mockAccount);

        SavingsAccount created = accountService.createSavingsAccount();

        assertEquals(67890, created.getAccountNumber());
        verify(savingsAccountDao, times(1)).save(any(SavingsAccount.class));
    }

    // --- TEST 3 : Dépôt Primary (Branche A) ---
    @Test
    void testDepositPrimary() {
        String username = "testUser";
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        User user = new User();
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal(0));
        user.setPrimaryAccount(primaryAccount);

        when(userService.findByUsername(username)).thenReturn(user);

        accountService.deposit("Primary", 100.0, mockPrincipal);

        assertEquals(new BigDecimal(100.0), primaryAccount.getAccountBalance());
        verify(transactionService, times(1)).savePrimaryDepositTransaction(any(PrimaryTransaction.class));
    }

    // --- TEST 4 : Dépôt Savings (Branche B - C'était manquant !) ---
    @Test
    void testDepositSavings() {
        String username = "testUser";
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        User user = new User();
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal(0));
        user.setSavingsAccount(savingsAccount);

        when(userService.findByUsername(username)).thenReturn(user);

        // On teste avec "Savings"
        accountService.deposit("Savings", 100.0, mockPrincipal);

        assertEquals(new BigDecimal(100.0), savingsAccount.getAccountBalance());
        verify(transactionService, times(1)).saveSavingsDepositTransaction(any(SavingsTransaction.class));
    }

    // --- TEST 5 : Retrait Primary (Branche C - C'était manquant !) ---
    @Test
    void testWithdrawPrimary() {
        String username = "testUser";
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        User user = new User();
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal(500));
        user.setPrimaryAccount(primaryAccount);

        when(userService.findByUsername(username)).thenReturn(user);

        // On teste le retrait Primary
        accountService.withdraw("Primary", 100.0, mockPrincipal);

        assertEquals(new BigDecimal(400.0), primaryAccount.getAccountBalance());
        verify(transactionService, times(1)).savePrimaryWithdrawTransaction(any(PrimaryTransaction.class));
    }

    // --- TEST 6 : Retrait Savings (Branche D) ---
    @Test
    void testWithdrawSavings() {
        String username = "testUser";
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        User user = new User();
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal(500.0));
        user.setSavingsAccount(savingsAccount);

        when(userService.findByUsername(username)).thenReturn(user);

        accountService.withdraw("Savings", 100.0, mockPrincipal);

        assertEquals(new BigDecimal(400.0), savingsAccount.getAccountBalance());
        verify(transactionService, times(1)).saveSavingsWithdrawTransaction(any(SavingsTransaction.class));
    }
}