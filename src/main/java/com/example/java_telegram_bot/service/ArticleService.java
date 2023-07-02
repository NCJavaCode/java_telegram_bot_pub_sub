package com.example.java_telegram_bot.service;

import com.example.java_telegram_bot.entity.Article;
import com.example.java_telegram_bot.entity.TelegramUser;

import java.util.List;

public interface ArticleService {

    void saveUserArticles(Long userID, List<String> listArticles);

    List<String> getAllArticlesLinks(Long userID);

    void deleteAllArticles(TelegramUser userID);

    List<Article> getAllArticles();

    List<Article> findArticlesByTelegramUserID(Long userID);

}
