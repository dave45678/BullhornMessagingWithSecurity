package com.example.demo.web.controller;

import com.example.demo.business.entities.User;
import com.example.demo.business.entities.repositories.MessageRepository;
import com.example.demo.business.entities.repositories.UserRepository;
import com.example.demo.business.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
public class LoginController {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/forgot-password")
    public String forgetPassword() {
        return "/";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("page_title", "New User Registration");
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user,
                                          BindingResult result,
                                          Model model/*,
                                          @RequestParam("password") String password*/
    ) {
        model.addAttribute("page_title", "Update Profile");
        if (result.hasErrors()) {
            return "register";
        } else {
            //Update User and Admin
            boolean isUser = userRepository.findById(user.getId()).isPresent();
            if (isUser) {
                //updating with existed username
                if (userRepository.findByUsername(user.getUsername()) != null &&
                        //current user
                        !userRepository.findByUsername(user.getUsername()).equals(user)) {
                    model.addAttribute("message",
                            "We already have a username called " + user.getUsername() + "!" + " Try something else.");
                    return "register";
                }

                User userInDB = userRepository.findById(user.getId()).get();
                userInDB.setFirstName(user.getFirstName());
                userInDB.setLastName(user.getLastName());
                userInDB.setEmail(user.getEmail());
                userInDB.setUsername(user.getUsername());
                userInDB.setPassword(userService.encode(user.getPassword()));
                userInDB.setEnabled(user.isEnabled());
                userRepository.save(userInDB);
                model.addAttribute("message", "User Account Successfully Updated");
            }
            //New User
            else {
                //Registering with existed username
                if (userRepository.findByUsername(user.getUsername()) != null) {
                    model.addAttribute("message",
                            "We already have a username called " + user.getUsername() + "!" + " Try something else.");
                    return "register";
                } else {
                    user.setPassword(userService.encode(user.getPassword()));
                    userService.saveUser(user);
                    model.addAttribute("message", "User Account Successfully Created");
                }
            }
        }
        return "login";
    }

    @GetMapping("/termsandconditions")
    public String getTermsAndCondition() {
        return "termsandconditions";
    }

    @RequestMapping("/updateUser")
    public String viewUser(Model model,
                           HttpServletRequest request,
                           Authentication authentication,
                           Principal principal) {
       /* Boolean isAdmin = request.isUserInRole("ADMIN");
        Boolean isUser = request.isUserInRole("USER");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();*/
//        String username = principal.getName();
        model.addAttribute("page_title", "Update Profile");
        model.addAttribute("user", userService.getUser());
        return "register";
    }
}
