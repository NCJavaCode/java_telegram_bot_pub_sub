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

//    private void sendLinks() {
//        try {
//            Document doc = SSLHelper.getConnection(botURL).get();
//            List<TelegramUser> telegramUsers = userService.getAllUsers();
//
//            for (TelegramUser telegramUser:telegramUsers) {
//                Long chatID = telegramUser.getTelegramUserID();
//
//                //exclude the user from sending messages if he stopped it using the /stop command
//               if(!telegramUser.isStart()) continue;
//
//                List<String> links = doc.select("div.ad a.btn-details").eachAttr("href");
//                links = links.size()<4?links.subList(0,links.size()):links.subList(0,3);
//
//                List<Article> used_links = articleService.findArticlesByTelegramUserID(chatID);
//                List<String> listUsedLinks = used_links.stream()
//                        .map(Article::getShortLink)
//                        .collect(Collectors.toList());
//
//                List<String> differences = links.stream()
//                        .filter(element -> !listUsedLinks.contains(element))
//                        .collect(Collectors.toList());
//
//                SendMessage message = new SendMessage();
//                message.enableMarkdown(true);
//                message.setChatId(chatID); //Write chatID manually here
//
//                for (String s:differences) {
//                    message.setText(botURLShort+s);
//                    execute(message);
//                }
//                articleService.saveUserArticles(chatID, differences);
//            }
//        }
//            catch (TelegramApiException e) {
//                e.printStackTrace();
//                log.error(e.getMessage());
//                log.error("Bot died!");
//            }
//            catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

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
