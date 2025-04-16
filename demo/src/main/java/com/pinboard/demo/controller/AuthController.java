package com.pinboard.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pinboard.demo.model.User;
import com.pinboard.demo.repository.UserRepository;
import com.pinboard.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("title", "Login");
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("title", "Cadastro");
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/login")
    @Transactional
    public String processLogin(@RequestParam String email, 
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        
        User user = userRepository.findUserByEmail(email);
        
        if (user != null && password.equals(user.getPassword())) {
            // Em uma aplicação real, verificaríamos o hash da senha
            
            // Forçar o carregamento das coleções que podem ser usadas na interface
            user.getPins().size();
            user.getBoards().size();
            user.getFollowers().size();
            user.getFollowing().size();
            user.getFavoritePins().size();
            user.getSavedPins().size();
            
            // Armazenar na sessão
            session.setAttribute("currentUser", user);
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Email ou senha incorretos");
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute User user, 
                                     RedirectAttributes redirectAttributes) {
        
        // Verificar se o email já está cadastrado
        if (userRepository.existsByEmail(user.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Este email já está cadastrado");
            return "redirect:/auth/register";
        }
        
        // Verificar se o username já está em uso
        if (userRepository.existsByUsername(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Este nome de usuário já está em uso");
            return "redirect:/auth/register";
        }
        
        // Em uma aplicação real, faríamos o hash da senha aqui
        userRepository.save(user);
        
        redirectAttributes.addFlashAttribute("success", "Cadastro realizado com sucesso! Faça login para continuar.");
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}