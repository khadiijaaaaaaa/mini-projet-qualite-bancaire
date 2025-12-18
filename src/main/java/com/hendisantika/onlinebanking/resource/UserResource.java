package com.hendisantika.onlinebanking.resource;

import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import com.hendisantika.onlinebanking.entity.SavingsTransaction;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.TransactionService;
import com.hendisantika.onlinebanking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : online-banking
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 04/09/18
 * Time: 06.39
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('ADMIN')")
public class UserResource {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    // CORRECTION : @GetMapping au lieu de RequestMethod.GET
    @GetMapping("/user/all")
    public List<User> userList() {
        return userService.findUserList();
    }

    // CORRECTION : @GetMapping
    @GetMapping("/user/primary/transaction")
    public List<PrimaryTransaction> getPrimaryTransactionList(@RequestParam("username") String username) {
        return transactionService.findPrimaryTransactionList(username);
    }

    // CORRECTION : @GetMapping
    @GetMapping("/user/savings/transaction")
    public List<SavingsTransaction> getSavingsTransactionList(@RequestParam("username") String username) {
        return transactionService.findSavingsTransactionList(username);
    }

    // CORRECTION : @GetMapping explicite (pour éviter que POST/DELETE soient acceptés par erreur)
    @GetMapping("/user/{username}/enable")
    public void enableUser(@PathVariable("username") String username) {
        userService.enableUser(username);
    }

    // CORRECTION : @GetMapping explicite
    @GetMapping("/user/{username}/disable")
    public void diableUser(@PathVariable("username") String username) {
        userService.disableUser(username);
    }
}