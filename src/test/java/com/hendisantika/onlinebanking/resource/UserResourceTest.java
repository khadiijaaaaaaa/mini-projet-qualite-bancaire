package com.hendisantika.onlinebanking.resource;

import com.hendisantika.onlinebanking.entity.PrimaryTransaction;
import com.hendisantika.onlinebanking.entity.SavingsTransaction;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.TransactionService;
import com.hendisantika.onlinebanking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserResourceTest {

    @InjectMocks
    private UserResource userResource;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    // --- TEST 1 : Récupérer tous les utilisateurs ---
    @Test
    void testUserList() {
        // Arrange
        List<User> mockList = new ArrayList<>();
        mockList.add(new User());
        mockList.add(new User());

        when(userService.findUserList()).thenReturn(mockList);

        // Act
        List<User> result = userResource.userList();

        // Assert
        assertEquals(2, result.size());
        verify(userService, times(1)).findUserList();
    }

    // --- TEST 2 : Récupérer transactions Primary d'un user ---
    @Test
    void testGetPrimaryTransactionList() {
        // Arrange
        String username = "testUser";
        List<PrimaryTransaction> mockList = new ArrayList<>();
        mockList.add(new PrimaryTransaction());

        when(transactionService.findPrimaryTransactionList(username)).thenReturn(mockList);

        // Act
        List<PrimaryTransaction> result = userResource.getPrimaryTransactionList(username);

        // Assert
        assertEquals(1, result.size());
        verify(transactionService, times(1)).findPrimaryTransactionList(username);
    }

    // --- TEST 3 : Récupérer transactions Savings d'un user ---
    @Test
    void testGetSavingsTransactionList() {
        // Arrange
        String username = "testUser";
        List<SavingsTransaction> mockList = new ArrayList<>();
        mockList.add(new SavingsTransaction());

        when(transactionService.findSavingsTransactionList(username)).thenReturn(mockList);

        // Act
        List<SavingsTransaction> result = userResource.getSavingsTransactionList(username);

        // Assert
        assertEquals(1, result.size());
        verify(transactionService, times(1)).findSavingsTransactionList(username);
    }

    // --- TEST 4 : Activer un utilisateur ---
    @Test
    void testEnableUser() {
        // Arrange
        String username = "toto";

        // Act
        userResource.enableUser(username);

        // Assert
        verify(userService, times(1)).enableUser(username);
    }

    // --- TEST 5 : Désactiver un utilisateur ---
    @Test
    void testDisableUser() {
        // Arrange
        String username = "toto";

        // Act
        // Attention : j'utilise le nom mal orthographié "diableUser" comme dans ton fichier source
        userResource.diableUser(username);

        // Assert
        verify(userService, times(1)).disableUser(username);
    }
}