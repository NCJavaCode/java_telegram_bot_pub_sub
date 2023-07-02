package com.example.java_telegram_bot.service;

import com.example.java_telegram_bot.entity.TelegramUser;

import java.util.List;

public interface UserService {

    TelegramUser createUser(TelegramUser telegramUser);

    boolean ifUserExists(Long telegramUserID);

    boolean ifStart(Long telegramUserID);

    void updateStartConditional(Long telegramUserID, boolean condition);

    Long dropUserFromChat(Long telegramUserID);

    TelegramUser getUser(Long telegramUserID);

    List<TelegramUser> getAllUsers();
}
