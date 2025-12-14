package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.PrimaryAccount;
import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import com.hendisantika.onlinebanking.entity.Recipient;
import com.hendisantika.onlinebanking.entity.SavingsAccount;
import com.hendisantika.onlinebanking.entity.SavingsTransaction;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.PrimaryAccountDao;
import com.hendisantika.onlinebanking.repository.PrimaryTransactionDao;
import com.hendisantika.onlinebanking.repository.RecipientDao;
import com.hendisantika.onlinebanking.repository.SavingsAccountDao;
import com.hendisantika.onlinebanking.repository.SavingsTransactionDao;
import com.hendisantika.onlinebanking.service.TransactionService;
import com.hendisantika.onlinebanking.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final UserService userService;
    private final PrimaryTransactionDao primaryTransactionDao;
    private final SavingsTransactionDao savingsTransactionDao;
    private final PrimaryAccountDao primaryAccountDao;
    private final SavingsAccountDao savingsAccountDao;
    private final RecipientDao recipientDao;

    public TransactionServiceImpl(
            @Qualifier("userServiceImpl") UserService userService,
            PrimaryTransactionDao primaryTransactionDao,
            SavingsTransactionDao savingsTransactionDao,
            PrimaryAccountDao primaryAccountDao,
            SavingsAccountDao savingsAccountDao,
            RecipientDao recipientDao
    ) {
        this.userService = userService;
        this.primaryTransactionDao = primaryTransactionDao;
        this.savingsTransactionDao = savingsTransactionDao;
        this.primaryAccountDao = primaryAccountDao;
        this.savingsAccountDao = savingsAccountDao;
        this.recipientDao = recipientDao;
    }

    @Override
    public List<PrimaryTransaction> findPrimaryTransactionList(String username) {
        User user = userService.findByUsername(username);
        return user.getPrimaryAccount().getPrimaryTransactionList();
    }

    @Override
    public List<SavingsTransaction> findSavingsTransactionList(String username) {
        User user = userService.findByUsername(username);
        return user.getSavingsAccount().getSavingsTransactionList();
    }

    @Override
    public void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionDao.save(primaryTransaction);
    }

    @Override
    public void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionDao.save(savingsTransaction);
    }

    @Override
    public void savePrimaryWithdrawTransaction(PrimaryTransaction primaryTransaction) {
        primaryTransactionDao.save(primaryTransaction);
    }

    @Override
    public void saveSavingsWithdrawTransaction(SavingsTransaction savingsTransaction) {
        savingsTransactionDao.save(savingsTransaction);
    }

    @Override
    public void betweenAccountsTransfer(
            String transferFrom,
            String transferTo,
            String amount,
            PrimaryAccount primaryAccount,
            SavingsAccount savingsAccount
    ) throws Exception {

        BigDecimal delta = new BigDecimal(amount).stripTrailingZeros();
        double amountDouble = Double.parseDouble(amount);

        if (transferFrom.equalsIgnoreCase("Primary") && transferTo.equalsIgnoreCase("Savings")) {

            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(delta));
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(delta));

            primaryAccountDao.save(primaryAccount);
            savingsAccountDao.save(savingsAccount);

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(
                    new Date(),
                    "Between account transfer from " + transferFrom + " to " + transferTo,
                    "Account",
                    "Finished",
                    amountDouble,
                    primaryAccount.getAccountBalance(),
                    primaryAccount
            );
            primaryTransactionDao.save(primaryTransaction);

        } else if (transferFrom.equalsIgnoreCase("Savings") && transferTo.equalsIgnoreCase("Primary")) {

            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(delta));
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(delta));

            primaryAccountDao.save(primaryAccount);
            savingsAccountDao.save(savingsAccount);

            SavingsTransaction savingsTransaction = new SavingsTransaction(
                    new Date(),
                    "Between account transfer from " + transferFrom + " to " + transferTo,
                    "Transfer",
                    "Finished",
                    amountDouble,
                    savingsAccount.getAccountBalance(),
                    savingsAccount
            );
            savingsTransactionDao.save(savingsTransaction);

        } else {
            throw new Exception("Invalid Transfer");
        }
    }

    @Override
    public List<Recipient> findRecipientList(Principal principal) {
        String username = principal.getName();
        return recipientDao.findAll().stream()
                .filter(recipient -> username.equals(recipient.getUser().getUsername()))
                .collect(Collectors.toList());
    }

    @Override
    public Recipient saveRecipient(Recipient recipient) {
        return recipientDao.save(recipient);
    }

    @Override
    public Recipient findRecipientByName(String recipientName) {
        return recipientDao.findByName(recipientName);
    }

    @Override
    public void deleteRecipientByName(String recipientName) {
        recipientDao.deleteByName(recipientName);
    }

    @Override
    public void toSomeoneElseTransfer(
            Recipient recipient,
            String accountType,
            String amount,
            PrimaryAccount primaryAccount,
            SavingsAccount savingsAccount
    ) {

        BigDecimal delta = new BigDecimal(amount).stripTrailingZeros();
        double amountDouble = Double.parseDouble(amount);

        if (accountType.equalsIgnoreCase("Primary")) {
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(delta));
            primaryAccountDao.save(primaryAccount);

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(
                    new Date(),
                    "Transfer to recipient " + recipient.getName(),
                    "Transfer",
                    "Finished",
                    amountDouble,
                    primaryAccount.getAccountBalance(),
                    primaryAccount
            );
            primaryTransactionDao.save(primaryTransaction);

        } else if (accountType.equalsIgnoreCase("Savings")) {
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(delta));
            savingsAccountDao.save(savingsAccount);

            SavingsTransaction savingsTransaction = new SavingsTransaction(
                    new Date(),
                    "Transfer to recipient " + recipient.getName(),
                    "Transfer",
                    "Finished",
                    amountDouble,
                    savingsAccount.getAccountBalance(),
                    savingsAccount
            );
            savingsTransactionDao.save(savingsTransaction);
        }
    }
}
