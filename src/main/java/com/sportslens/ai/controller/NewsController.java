package com.sportslens.ai.controller;

import com.sportslens.ai.domain.BrowsingHistory;
import com.sportslens.ai.domain.NewsArticle;
import com.sportslens.ai.domain.User;
import com.sportslens.ai.repository.BrowsingHistoryRepository;
import com.sportslens.ai.repository.NewsArticleRepository;
import com.sportslens.ai.repository.UserRepository;
import com.sportslens.ai.service.NewsArticleService;
import com.sportslens.ai.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Controller
public class NewsController {

    @Autowired
    private NewsArticleRepository newsArticleRepository;

    @Autowired
    private NewsArticleService newsArticleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrowsingHistoryRepository browsingHistoryRepository;

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<NewsArticle> articles = newsArticleService.findAll();
        model.addAttribute("articles", articles);

        if (userDetails != null) {
            userRepository.findByUsername(userDetails.getUsername()).ifPresent(user -> {
                List<NewsArticle> recommendedArticles = recommendationService.getRecommendationsForUser(user, 5);
                model.addAttribute("recommendedArticles", recommendedArticles);
            });
        }

        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<NewsArticle> articles = newsArticleService.searchArticles(query);
        model.addAttribute("articles", articles);
        model.addAttribute("searchQuery", query);
        return "index";
    }

    @GetMapping("/article/{id}")
    public RedirectView viewArticle(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<NewsArticle> optionalArticle = newsArticleRepository.findById(id);

        if (optionalArticle.isEmpty()) {
            return new RedirectView("/");
        }

        NewsArticle article = optionalArticle.get();

        if (userDetails != null) {
            Optional<User> optionalUser = userRepository.findByUsername(userDetails.getUsername());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                BrowsingHistory history = new BrowsingHistory(user, article);
                browsingHistoryRepository.save(history);
            }
        }

        return new RedirectView(article.getUrl());
    }
}