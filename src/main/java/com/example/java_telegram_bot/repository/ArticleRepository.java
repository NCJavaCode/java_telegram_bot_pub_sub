package com.example.java_telegram_bot.repository;

import com.example.java_telegram_bot.entity.Article;
import com.example.java_telegram_bot.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {

    long deleteByTelegramUser(TelegramUser telegramUser);

    List<Article> findByTelegramUser_TelegramUserID(Long telegramUserID);
}