package com.example.java_telegram_bot.factory;

import com.example.java_telegram_bot.entity.TelegramUser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramUserFactory {
    public TelegramUser createInstance(Update update) throws Exception {

        Message message = update.getMessage();

        Long userID = message.getFrom().getId();

        String firstName = message.getFrom().getFirstName();
        String lastName = message.getFrom().getLastName();
        String userName = message.getFrom().getUserName();

        TelegramUser user = new TelegramUser();
        user.setUserName(userName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setTelegramUserID(userID);
        user.setStart(true);

        return user;
    }
}
