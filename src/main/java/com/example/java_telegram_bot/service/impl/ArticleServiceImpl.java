package com.example.java_telegram_bot.service.impl;

import com.example.java_telegram_bot.entity.Article;
import com.example.java_telegram_bot.entity.TelegramUser;
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

    @Override
    public void saveUserArticles(Long userID, List<String> listArticles){
        Article article;
        TelegramUser telegramUser = telegramUserRepository.findByTelegramUserID(userID);
        for (String s:listArticles) {

            article = new Article();
            article.setTelegramUser(telegramUser);
            article.setLinkID(Integer.parseInt(s.substring(4)));
            article.setShortLink(s);
            article.setLink("https://metr.ua/"+s);

            articleRepository.save(article);
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
