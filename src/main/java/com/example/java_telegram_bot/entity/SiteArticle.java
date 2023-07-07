package com.example.java_telegram_bot.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
@Table(name = "site_article")
public class SiteArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name = "article_id")
    private int ID;

    @Column(name = "link_id", nullable = false)
    private int linkID;

    @Column(name = "link", nullable = false, length = 1000)
    private String link;

    @Column(name = "short_link", nullable = false, length = 1000)
    private String shortLink;
}
