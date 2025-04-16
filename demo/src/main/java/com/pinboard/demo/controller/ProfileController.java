package com.pinboard.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pinboard.demo.model.User;
import com.pinboard.demo.service.PinService;
import com.pinboard.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PinService pinService;
    
    // Exibe o perfil do usuário logado
    @GetMapping("")
    public String currentUserProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        return "redirect:/profile/" + currentUser.getUsername();
    }
    
    // Exibe o perfil de um usuário pelo username
    @GetMapping("/{username}")
    @Transactional
    public String viewUserProfile(@PathVariable String username, 
                                  HttpSession session, 
                                  Model model) {
        
        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            return "error/404";
        }
        
        User user = userOpt.get();
        
        // Carregar de forma explícita as coleções lazy para evitar LazyInitializationException
        user.getPins().size(); // Isso força o carregamento da coleção
        user.getBoards().size();
        user.getFollowers().size();
        user.getFollowing().size();
        
        model.addAttribute("profileUser", user);
        model.addAttribute("title", user.getUsername() + " - Perfil");
        
        // Adiciona informações para a aba ativa
        model.addAttribute("activeTab", "pins");
        
        // Verifica se o usuário atual está seguindo este perfil
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            // Recarrega o usuário atual para ter acesso às coleções em uma transação ativa
            currentUser = userService.refreshUser(currentUser.getId());
            model.addAttribute("isFollowing", currentUser.getFollowing().contains(user));
            model.addAttribute("isOwnProfile", currentUser.getId().equals(user.getId()));
            
            // Atualiza o usuário na sessão com a versão mais recente
            session.setAttribute("currentUser", currentUser);
        } else {
            model.addAttribute("isFollowing", false);
            model.addAttribute("isOwnProfile", false);
        }
        
        return "profile/show";
    }
    
    // Exibe os pins favoritos do usuário
    @GetMapping("/{username}/favorites")
    @Transactional
    public String viewUserFavorites(@PathVariable String username, 
                                    HttpSession session, 
                                    Model model) {
        
        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            return "error/404";
        }
        
        User user = userOpt.get();
        
        // Carregar de forma explícita as coleções lazy
        user.getFavoritePins().size();
        user.getFollowers().size();
        user.getFollowing().size();
        
        model.addAttribute("profileUser", user);
        model.addAttribute("title", user.getUsername() + " - Favoritos");
        
        // Define a aba ativa
        model.addAttribute("activeTab", "favorites");
        
        // Verifica se o usuário atual está seguindo este perfil
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            // Recarrega o usuário atual para ter acesso às coleções em uma transação ativa
            currentUser = userService.refreshUser(currentUser.getId());
            model.addAttribute("isFollowing", currentUser.getFollowing().contains(user));
            model.addAttribute("isOwnProfile", currentUser.getId().equals(user.getId()));
            
            // Atualiza o usuário na sessão com a versão mais recente
            session.setAttribute("currentUser", currentUser);
        } else {
            model.addAttribute("isFollowing", false);
            model.addAttribute("isOwnProfile", false);
        }
        
        return "profile/show";
    }
    
    // Exibe os pins salvos do usuário
    @GetMapping("/{username}/saved")
    @Transactional
    public String viewUserSaved(@PathVariable String username, 
                                HttpSession session, 
                                Model model) {
        
        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            return "error/404";
        }
        
        User user = userOpt.get();
        
        // Carregar de forma explícita as coleções lazy
        user.getSavedPins().size();
        user.getFollowers().size();
        user.getFollowing().size();
        
        model.addAttribute("profileUser", user);
        model.addAttribute("title", user.getUsername() + " - Salvos");
        
        // Define a aba ativa
        model.addAttribute("activeTab", "saved");
        
        // Verifica se o usuário atual está seguindo este perfil
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            // Recarrega o usuário atual para ter acesso às coleções em uma transação ativa
            currentUser = userService.refreshUser(currentUser.getId());
            model.addAttribute("isFollowing", currentUser.getFollowing().contains(user));
            model.addAttribute("isOwnProfile", currentUser.getId().equals(user.getId()));
            
            // Atualiza o usuário na sessão com a versão mais recente
            session.setAttribute("currentUser", currentUser);
        } else {
            model.addAttribute("isFollowing", false);
            model.addAttribute("isOwnProfile", false);
        }
        
        return "profile/show";
    }
    
    // Seguir um usuário
    @PostMapping("/follow/{username}")
    @Transactional
    public String followUser(@PathVariable String username, 
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        // Recarregar o usuário atual para ter certeza de que temos a versão mais recente
        currentUser = userService.refreshUser(currentUser.getId());
        
        Optional<User> userToFollowOpt = userService.getUserByUsername(username);
        if (userToFollowOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado");
            return "redirect:/";
        }
        
        User userToFollow = userToFollowOpt.get();
        
        // Evitar que um usuário siga a si mesmo
        if (currentUser.getId().equals(userToFollow.getId())) {
            redirectAttributes.addFlashAttribute("error", "Você não pode seguir a si mesmo");
            return "redirect:/profile/" + username;
        }
        
        // Verificar se já está seguindo
        if (currentUser.getFollowing().contains(userToFollow)) {
            currentUser.unfollow(userToFollow);
        } else {
            currentUser.follow(userToFollow);
        }
        
        userService.saveUser(currentUser);
        
        // Atualiza o usuário na sessão
        session.setAttribute("currentUser", currentUser);
        
        return "redirect:/profile/" + username;
    }
}