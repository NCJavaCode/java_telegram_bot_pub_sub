package com.example.java_telegram_bot.repository;

import com.example.java_telegram_bot.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {

    long deleteByTelegramUserID(Long telegramUserID);
    TelegramUser findByTelegramUserID(Long telegramUserID);

    boolean existsByTelegramUserID(Long telegramUserID);

    boolean existsByTelegramUserIDAndIsStartTrue(Long telegramUserID);

    @Transactional
    @Modifying
    @Query("update TelegramUser t set t.isStart = ?1 where t.telegramUserID = ?2")
    int updateIsStartByTelegramUserID(@NonNull boolean isStart, Long telegramUserID);
}