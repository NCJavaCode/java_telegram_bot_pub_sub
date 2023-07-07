package com.example.java_telegram_bot.service.impl;

import com.example.java_telegram_bot.entity.SiteArticle;
import com.example.java_telegram_bot.repository.SiteArticleRepository;
import com.example.java_telegram_bot.service.SiteArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SiteArticleServiceImpl implements SiteArticleService{

    @Autowired
    SiteArticleRepository articleRepository;

    public List<SiteArticle> getLimitedSiteArticle (int limit){
        Pageable topX = PageRequest.of(0, limit);
        return articleRepository.findAll(topX).getContent();
    }

    public List<SiteArticle> getAllSiteArticle(){
        return articleRepository.findAll();
    }

}
