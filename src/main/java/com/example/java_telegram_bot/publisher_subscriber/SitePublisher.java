package com.example.java_telegram_bot.publisher_subscriber;

import com.example.java_telegram_bot.Bot;
import com.example.java_telegram_bot.entity.SiteArticle;
import com.example.java_telegram_bot.entity.TelegramUser;
import com.example.java_telegram_bot.helper.SSLHelper;
import com.example.java_telegram_bot.repository.SiteArticleRepository;
import com.example.java_telegram_bot.service.impl.SiteArticleServiceImpl;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@EnableScheduling
public class SitePublisher {

    @Autowired
    SiteArticleServiceImpl siteArticleService;

    @Autowired
    SiteArticleRepository articleRepository;

    @Setter
    private Bot bot;

    private List<TelegramUser> subscribers;

    @Value("${bot.url.short}")
    private String botURLShort;

    @Value("${bot.url}")
    private String botURL;

    public SitePublisher() {
        subscribers = new ArrayList<>();
    }

    public void addObserver(TelegramUser participant){
        subscribers.add(participant);
        notifyImmediately(participant.getTelegramUserID());
    }

    public void removeObserver(TelegramUser participant){
        if (subscribers.size() != 0)
            subscribers.remove(participant);
    }

    private void notifyImmediately(long chatID){
        List<SiteArticle> article = siteArticleService.getLimitedSiteArticle(3);
        article.stream().forEach(siteArticle -> bot.sendMessage(chatID, siteArticle.getLink()));
    }

    private void notify(long chatID, List<SiteArticle> siteArticles){
        siteArticles.stream().forEach(siteArticle -> bot.sendMessage(chatID, siteArticle.getLink()));
    }

    @Scheduled(cron = "${schedule.cron.interval}")
    public void updateSiteArticles() throws IOException {
        Document doc = SSLHelper.getConnection(botURL).get();

        //get all saved links from DB
        List<SiteArticle> usedSiteArticleLinks = siteArticleService.getAllSiteArticle();
        //get last links from site
        List<String> links = doc.select("div.ad a.btn-details").eachAttr("href");
        //get all short links from DB
        List<String> listUsedLinks = usedSiteArticleLinks.stream()
                .map(SiteArticle::getShortLink)
                .collect(Collectors.toList());
        //get records that are not in the database, but are on the site
        List<String> differences = links.stream()
                .filter(element -> !listUsedLinks.contains(element))
                .collect(Collectors.toList());

        //based on the difference between records in the DB and on the site, create instances of SiteArticle to add to the DB
        List<SiteArticle> siteArticles =
                differences.stream()
                .map((short_link) -> {SiteArticle article = new SiteArticle();
                            article.setShortLink(short_link);
                            article.setLink(botURLShort+short_link);
                            article.setLinkID(Integer.parseInt(short_link.substring(4)));
                            return article;})
                .collect(Collectors.toList());


        articleRepository.saveAllAndFlush(siteArticles);

        if (differences.size() != 0)
            notifySub(siteArticles);

    }

    private void notifySub(List<SiteArticle> siteArticle){
        log.info("A new ad has appeared! We notify subscribers!");
        for (TelegramUser participant:subscribers) {
            participant.update("A new ad has appeared!");
            notify(participant.getTelegramUserID(), siteArticle);
        }
    }
}
