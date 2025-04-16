package com.pinboard.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pinboard.demo.model.Board;
import com.pinboard.demo.model.User;
import com.pinboard.demo.pattern.singleton.ConfigurationManager;
import com.pinboard.demo.service.BoardService;
import com.pinboard.demo.service.PinService;

@Controller
@RequestMapping("/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;
    
    @Autowired
    private PinService pinService;
    
    // Obtém a instância do Singleton para configurações
    private final ConfigurationManager configManager = ConfigurationManager.getInstance();
    
    @GetMapping
    public String getAllBoards(Model model) {
        List<Board> boards = boardService.getPublicBoards();
        model.addAttribute("boards", boards);
        model.addAttribute("appName", configManager.getAppName());
        return "boards/index";
    }
    
    @GetMapping("/{id}")
    public String getBoard(@PathVariable Long id, @RequestParam(required = false) String layout, Model model) {
        Board board = boardService.getBoard(id);
        if (board == null) {
            return "redirect:/boards";
        }
        
        // Utiliza o padrão Factory para criar o layout adequado
        String layoutType = layout != null ? layout : "masonry";
        String boardLayout = boardService.renderBoardLayout(layoutType);
        
        model.addAttribute("board", board);
        model.addAttribute("boardLayoutHtml", boardLayout);
        model.addAttribute("pins", board.getPins());
        model.addAttribute("appName", configManager.getAppName());
        
        return "boards/show";
    }
    
    @GetMapping("/new")
    public String newBoardForm(Model model) {
        model.addAttribute("board", new Board());
        model.addAttribute("appName", configManager.getAppName());
        return "boards/new";
    }
    
    @PostMapping
    public String createBoard(@ModelAttribute Board board) {
        // Em uma aplicação real, obteríamos o usuário da sessão
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("usuarioDemo");
        
        boardService.createBoard(board, mockUser);
        return "redirect:/boards";
    }
    
    @GetMapping("/{id}/edit")
    public String editBoardForm(@PathVariable Long id, Model model) {
        Board board = boardService.getBoard(id);
        if (board == null) {
            return "redirect:/boards";
        }
        
        model.addAttribute("board", board);
        model.addAttribute("appName", configManager.getAppName());
        return "boards/edit";
    }
    
    @PostMapping("/{id}")
    public String updateBoard(@PathVariable Long id, @ModelAttribute Board board) {
        board.setId(id);
        boardService.updateBoard(board);
        return "redirect:/boards/" + id;
    }
    
    @GetMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return "redirect:/boards";
    }
    
    @GetMapping("/{boardId}/add-pin/{pinId}")
    public String addPinToBoard(@PathVariable Long boardId, @PathVariable Long pinId) {
        boardService.addPinToBoard(boardId, pinId);
        return "redirect:/boards/" + boardId;
    }
    
    @GetMapping("/{boardId}/remove-pin/{pinId}")
    public String removePinFromBoard(@PathVariable Long boardId, @PathVariable Long pinId) {
        boardService.removePinFromBoard(boardId, pinId);
        return "redirect:/boards/" + boardId;
    }
    
    @GetMapping("/search")
    public String searchBoards(@RequestParam String keyword, Model model) {
        List<Board> boards = boardService.searchBoards(keyword);
        model.addAttribute("boards", boards);
        model.addAttribute("keyword", keyword);
        model.addAttribute("appName", configManager.getAppName());
        return "boards/search";
    }
}