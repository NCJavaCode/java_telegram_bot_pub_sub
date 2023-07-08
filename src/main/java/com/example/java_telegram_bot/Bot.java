package com.example.java_telegram_bot;

import com.example.java_telegram_bot.entity.TelegramUser;
import com.example.java_telegram_bot.factory.TelegramUserFactory;
import com.example.java_telegram_bot.publisher_subscriber.SitePublisher;
import com.example.java_telegram_bot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
public class Bot extends TelegramLongPollingBot  {

    @Autowired
    SitePublisher sitePublisher;

    @Autowired
    TelegramUserFactory telegramUserFactory;

    @Autowired
    UserService userService;

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
            TelegramUser user = userService.getUser(update.getMyChatMember().getChat().getId());
            //delete chat data from the database if the user left the chat with the bot
            if (update.getMyChatMember().getNewChatMember().getStatus().equals("kicked")) {
                log.info("User left the chat");
                sitePublisher.removeObserver(user);
            } else {
                log.info("User join to chat");
            }
        }

       if (update.getMessage() != null && update.getMessage().hasText()) {

           String messageText = update.getMessage().getText();

           switch (messageText) {
               case "/start":
                   {
                       TelegramUser user = telegramUserFactory.createInstance(update);
                       sitePublisher.addObserver(user);
                   }
                   break;

               case "/stop":
                   TelegramUser user = telegramUserFactory.createInstance(update);
                   sitePublisher.removeObserver(user);
                   break;

               default:
                   log.info("Unexpected message");
           }
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
        log.info("Bot registered!");
        sitePublisher.setBot(this);
        super.onRegister();
    }
}
