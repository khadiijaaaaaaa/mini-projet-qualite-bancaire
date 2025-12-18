package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.dto.UserProfileForm;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(
            @Qualifier("userServiceImpl") UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String profilePost(@ModelAttribute("user") UserProfileForm userForm, Model model) {
        // CORRECTION : On reçoit le DTO userForm au lieu de l'Entité User

        // On cherche l'utilisateur en base (l'Entité persistante)
        User user = userService.findByUsername(userForm.getUsername());

        // MAPPING MANUEL : On met à jour l'entité avec les données du DTO
        // Seuls ces champs seront modifiés. Impossible de modifier l'ID ou le solde.
        user.setUsername(userForm.getUsername());
        user.setFirstName(userForm.getFirstName());
        user.setLastName(userForm.getLastName());
        user.setEmail(userForm.getEmail());
        user.setPhone(userForm.getPhone());

        userService.saveUser(user);

        // On renvoie l'entité mise à jour à la vue
        model.addAttribute("user", user);

        return "profile";
    }
}