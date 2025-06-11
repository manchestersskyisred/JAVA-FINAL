package com.sportslens.ai.controller;

import com.sportslens.ai.domain.BrowsingHistory;
import com.sportslens.ai.domain.User;
import com.sportslens.ai.dto.UserRegistrationDto;
import com.sportslens.ai.repository.BrowsingHistoryRepository;
import com.sportslens.ai.repository.UserRepository;
import com.sportslens.ai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrowsingHistoryRepository browsingHistoryRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserRegistrationDto userDto, BindingResult bindingResult, Model model) {
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            model.addAttribute("errorMessage", "两次输入的密码不匹配");
            return "register";
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            userService.registerNewUser(userDto);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
        return "redirect:/login?registration_success";
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> optionalUser = userRepository.findByUsername(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        List<BrowsingHistory> history = browsingHistoryRepository.findByUserOrderByViewedAtDesc(user);

        model.addAttribute("user", user);
        model.addAttribute("history", history);

        return "profile";
    }
}