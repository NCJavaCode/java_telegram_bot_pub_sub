package com.example.java_telegram_bot.service.impl;

import com.example.java_telegram_bot.entity.Article;
import com.example.java_telegram_bot.entity.TelegramUser;
import com.example.java_telegram_bot.factory.ArticleFactory;
import com.example.java_telegram_bot.repository.ArticleRepository;
import com.example.java_telegram_bot.repository.TelegramUserRepository;
import com.example.java_telegram_bot.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService{

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    TelegramUserRepository telegramUserRepository;

    @Autowired
    ArticleFactory articleFactory;

    @Override
    public void saveUserArticles(Long userID, List<String> listArticles){
        TelegramUser telegramUser = telegramUserRepository.findByTelegramUserID(userID);
        for (String s:listArticles) {
           articleRepository.save(articleFactory.createInstance(telegramUser, s));
        }
    }

    @Override
    public List<String> getAllArticlesLinks(Long UserID) {
        return null;
    }

    @Override
    public void deleteAllArticles(TelegramUser userID) {
        articleRepository.deleteByTelegramUser(userID);
    }

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public List<Article> findArticlesByTelegramUserID(Long userID) {
        return articleRepository.findByTelegramUser_TelegramUserID(userID);
    }
}
