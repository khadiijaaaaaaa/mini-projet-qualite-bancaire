package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.SavingsTransaction;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.PrimaryAccountDao;
import com.hendisantika.onlinebanking.repository.SavingsAccountDao;
import com.hendisantika.onlinebanking.service.AccountService;
import com.hendisantika.onlinebanking.service.TransactionService;
import com.hendisantika.onlinebanking.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {

    // CORRECTION (S1192): Création de constantes pour les littéraux dupliqués
    private static final String ACCOUNT = "Account";
    private static final String FINISHED = "Finished";
    private static final String PRIMARY = "Primary";
    private static final String SAVINGS = "Savings";

    // Variable statique partagée pour la génération de numéros de compte
    private static int nextAccountNumber = 11223101;

    private final PrimaryAccountDao primaryAccountDao;
    private final SavingsAccountDao savingsAccountDao;
    private final UserService userService;
    private final TransactionService transactionService;

    public AccountServiceImpl(
            PrimaryAccountDao primaryAccountDao,
            SavingsAccountDao savingsAccountDao,
            @Qualifier("userServiceImpl") UserService userService,
            TransactionService transactionService
    ) {
        this.primaryAccountDao = primaryAccountDao;
        this.savingsAccountDao = savingsAccountDao;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @Override
    public PrimaryAccount createPrimaryAccount() {
        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(BigDecimal.valueOf(0));
        primaryAccount.setAccountNumber(accountGen());

        primaryAccountDao.save(primaryAccount);

        return primaryAccountDao.findByAccountNumber(primaryAccount.getAccountNumber());
    }

    @Override
    public SavingsAccount createSavingsAccount() {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(BigDecimal.valueOf(0));
        savingsAccount.setAccountNumber(accountGen());

        savingsAccountDao.save(savingsAccount);

        return savingsAccountDao.findByAccountNumber(savingsAccount.getAccountNumber());
    }

    @Override
    public void deposit(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        BigDecimal delta = BigDecimal.valueOf(amount).stripTrailingZeros();

        if (accountType.equalsIgnoreCase(PRIMARY)) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(
                    primaryAccount.getAccountBalance().add(delta)
            );
            primaryAccountDao.save(primaryAccount);

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(
                    new Date(),
                    "Deposit to Primary Account",
                    ACCOUNT,
                    FINISHED,
                    amount,
                    primaryAccount.getAccountBalance(),
                    primaryAccount
            );
            transactionService.savePrimaryDepositTransaction(primaryTransaction);

        } else if (accountType.equalsIgnoreCase(SAVINGS)) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(
                    savingsAccount.getAccountBalance().add(delta)
            );
            savingsAccountDao.save(savingsAccount);

            SavingsTransaction savingsTransaction = new SavingsTransaction(
                    new Date(),
                    "Deposit to savings Account",
                    ACCOUNT,
                    FINISHED,
                    amount,
                    savingsAccount.getAccountBalance(),
                    savingsAccount
            );
            transactionService.saveSavingsDepositTransaction(savingsTransaction);
        }
    }

    @Override
    public void withdraw(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        BigDecimal delta = BigDecimal.valueOf(amount).stripTrailingZeros();

        if (accountType.equalsIgnoreCase(PRIMARY)) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(
                    primaryAccount.getAccountBalance().subtract(delta)
            );
            primaryAccountDao.save(primaryAccount);

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(
                    new Date(),
                    "Withdraw from Primary Account",
                    ACCOUNT,
                    FINISHED,
                    amount,
                    primaryAccount.getAccountBalance(),
                    primaryAccount
            );
            transactionService.savePrimaryWithdrawTransaction(primaryTransaction);

        } else if (accountType.equalsIgnoreCase(SAVINGS)) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(
                    savingsAccount.getAccountBalance().subtract(delta)
            );
            savingsAccountDao.save(savingsAccount);

            SavingsTransaction savingsTransaction = new SavingsTransaction(
                    new Date(),
                    "Withdraw from savings Account",
                    ACCOUNT,
                    FINISHED,
                    amount,
                    savingsAccount.getAccountBalance(),
                    savingsAccount
            );
            transactionService.saveSavingsWithdrawTransaction(savingsTransaction);
        }
    }

    // CORRECTION (S2696): La méthode doit être statique pour modifier un champ statique
    // Ajout de 'synchronized' pour la sécurité en environnement multi-thread
    private static synchronized int accountGen() {
        return ++nextAccountNumber;
    }
}