package com.example.java_telegram_bot.service.impl;

import com.example.java_telegram_bot.entity.TelegramUser;
import com.example.java_telegram_bot.repository.TelegramUserRepository;
import com.example.java_telegram_bot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    TelegramUserRepository telegramUserRepository;

    @Override
    public TelegramUser createUser(TelegramUser telegramUser) {
        return telegramUserRepository.save(telegramUser);
    }

    @Override
    public boolean ifUserExists(Long telegramUserID) {
        return telegramUserRepository.existsByTelegramUserID(telegramUserID);
    }

    @Override
    public boolean ifStart(Long telegramUserID) {
        return telegramUserRepository.existsByTelegramUserIDAndIsStartTrue(telegramUserID);
    }

    @Override
    public void updateStartConditional(Long telegramUserID, boolean condition) {
        telegramUserRepository.updateIsStartByTelegramUserID(condition, telegramUserID);
    }

    @Override
    public Long dropUserFromChat(Long telegramUserID) {
        return telegramUserRepository.deleteByTelegramUserID(telegramUserID);
    }

    @Override
    public TelegramUser getUser(Long telegramUserID) {
        return telegramUserRepository.findByTelegramUserID(telegramUserID);
    }

    public List<TelegramUser> getAllUsers() {
        return telegramUserRepository.findAll();
    }

}
