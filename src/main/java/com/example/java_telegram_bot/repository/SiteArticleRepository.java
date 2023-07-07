package com.example.java_telegram_bot.repository;

import com.example.java_telegram_bot.entity.SiteArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteArticleRepository extends JpaRepository<SiteArticle, Integer> {
}