package com.hendisantika.onlinebanking.controller;

import com.hendisantika.onlinebanking.dto.UserSignupForm;
import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.RoleDao;
import com.hendisantika.onlinebanking.security.UserRole;
import com.hendisantika.onlinebanking.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
public class HomeController {

    private final UserService userService;
    private final RoleDao roleDao;

    public HomeController(
            @Qualifier("userServiceImpl") UserService userService,
            RoleDao roleDao
    ) {
        this.userService = userService;
        this.roleDao = roleDao;
    }

    // CORRECTION : @GetMapping pour la page d'accueil
    @GetMapping("/")
    public String home() {
        return "redirect:/index";
    }

    // CORRECTION : @GetMapping pour l'index
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new UserSignupForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupPost(@ModelAttribute("user") UserSignupForm userForm, Model model) {
        if (userService.checkUserExists(userForm.getUsername(), userForm.getEmail())) {
            if (userService.checkEmailExists(userForm.getEmail())) {
                model.addAttribute("emailExists", true);
            }
            if (userService.checkUsernameExists(userForm.getUsername())) {
                model.addAttribute("usernameExists", true);
            }
            return "signup";
        }

        User user = new User();
        user.setUsername(userForm.getUsername());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setFirstName(userForm.getFirstName());
        user.setLastName(userForm.getLastName());
        user.setPhone(userForm.getPhone());

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, roleDao.findByName("ROLE_USER")));

        userService.createUser(user, userRoles);

        return "redirect:/";
    }

    @GetMapping("/userFront")
    public String userFront(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("primaryAccount", user.getPrimaryAccount());
        model.addAttribute("savingsAccount", user.getSavingsAccount());
        return "userFront";
    }
}