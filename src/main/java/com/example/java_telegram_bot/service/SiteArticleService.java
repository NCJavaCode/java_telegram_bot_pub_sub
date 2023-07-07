package com.example.java_telegram_bot.service;

import com.example.java_telegram_bot.entity.SiteArticle;
import java.util.List;

public interface SiteArticleService {
    List<SiteArticle> getLimitedSiteArticle (int limit);

    List<SiteArticle> getAllSiteArticle();
}
