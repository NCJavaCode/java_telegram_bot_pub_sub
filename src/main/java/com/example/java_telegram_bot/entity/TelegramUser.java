package com.example.java_telegram_bot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "telegram_user")
public class TelegramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name = "user_id")
    private @NonNull Long ID;

    @Column(name = "first_name", nullable = false)
    private @NonNull String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "telegram_user_id", nullable = false)
    private Long telegramUserID;

    @Column(name = "is_start", nullable = false)
    private boolean isStart;
}
