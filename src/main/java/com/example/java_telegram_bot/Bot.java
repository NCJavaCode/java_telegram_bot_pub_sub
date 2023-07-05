package com.example.java_telegram_bot;

import com.example.java_telegram_bot.entity.Article;
import com.example.java_telegram_bot.entity.TelegramUser;
import com.example.java_telegram_bot.factory.TelegramUserFactory;
import com.example.java_telegram_bot.helper.SSLHelper;
import com.example.java_telegram_bot.service.ArticleService;
import com.example.java_telegram_bot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@EnableScheduling
public class Bot extends TelegramLongPollingBot  {

    @Autowired
    TelegramUserFactory telegramUserFactory;

    @Autowired
    UserService userService;

    @Autowired
    ArticleService articleService;

    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.url}")
    private String botURL;

    @Value("${bot.url.short}")
    private String botURLShort;

    @Override
    public void onUpdateReceived(Update update) {

        //if the participant joined or left the chat
        if (update.hasMyChatMember()) {
            //delete chat data from the database if the user left the chat with the bot
            if (update.getMyChatMember().getNewChatMember().getStatus().equals("kicked")) {
                TelegramUser user = userService.getUser(update.getMyChatMember().getChat().getId());
                articleService.deleteAllArticles(user);
                userService.dropUserFromChat(update.getMyChatMember().getChat().getId());
            } else {
                log.info("User join to chat");
            }
        }

       if (update.getMessage().hasText()) {

           long chatId = update.getMessage().getChatId();
           Long userID = update.getMessage().getFrom().getId();

           String messageText = update.getMessage().getText();

           switch (messageText) {
               case "/start":
                   if (userService.ifStart(userID)) {
                       sendMessage(chatId, "You already started chat! Enjoy:)");
                   }
                   if (userService.ifUserExists(chatId)) {
                       userService.updateStartConditional(userID, true);
                       log.info("User exist!");
                   } else {
                       userService.createUser(telegramUserFactory.createInstance(update));
                   }
                   break;

               case "/stop":
                   userService.updateStartConditional(userID, false);
                   sendMessage(chatId, "Your distribution is paused...");
                   break;

               default:
                   log.info("Unexpected message");
           }
       }
    }

    @Scheduled(cron = "${schedule.cron.interval}")
    public void sendMessage() {
        log.info("Looping...");
        sendLinks();
    }

    @Override
    public void onClosing() {
        super.onClosing();
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    private void sendLinks() {
        try {
            Document doc = SSLHelper.getConnection(botURL).get();
            List<TelegramUser> telegramUsers = userService.getAllUsers();

            for (TelegramUser telegramUser:telegramUsers) {
                Long chatID = telegramUser.getTelegramUserID();

                //exclude the user from sending messages if he stopped it using the /stop command
               if(!telegramUser.isStart()) continue;

                List<String> links = doc.select("div.ad a.btn-details").eachAttr("href");
                links = links.size()<4?links.subList(0,links.size()):links.subList(0,3);

                List<Article> used_links = articleService.findArticlesByTelegramUserID(chatID);
                List<String> listUsedLinks = used_links.stream().map(Article::getShortLink).collect(Collectors.toList());

                List<String> differences = links.stream()
                        .filter(element -> !listUsedLinks.contains(element))
                        .collect(Collectors.toList());

                SendMessage message = new SendMessage();
                message.enableMarkdown(true);
                message.setChatId(chatID); //Write chatID manually here

                for (String s:differences) {
                    message.setText(botURLShort+s);
                    execute(message);
                }
                articleService.saveUserArticles(chatID, differences);
            }
        }
            catch (TelegramApiException e) {
                e.printStackTrace();
                log.error(e.getMessage());
                log.error("Bot died!");
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    public void sendMessage(Long chatId, String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textMessage);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
