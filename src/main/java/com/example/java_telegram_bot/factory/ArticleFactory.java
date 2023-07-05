package com.example.java_telegram_bot.factory;

import com.example.java_telegram_bot.entity.Article;
import com.example.java_telegram_bot.entity.TelegramUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ArticleFactory {

    @Value("${bot.url.short}")
    private String botURLShort;

    public Article createInstance(TelegramUser telegramUser, String url_id) {

        Article article = new Article();
        article.setTelegramUser(telegramUser);
        article.setLinkID(Integer.parseInt(url_id.substring(4)));
        article.setShortLink(url_id);
        article.setLink(botURLShort+url_id);

        return article;
    }
}
