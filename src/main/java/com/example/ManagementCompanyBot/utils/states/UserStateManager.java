package com.example.ManagementCompanyBot.utils.states;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserStateManager {

    private final ConcurrentHashMap<Long, BotStates> userStates = new ConcurrentHashMap<>();

    public BotStates getUserState(Long chatId) {
        return userStates.getOrDefault(chatId, BotStates.IDLE);
    }

    public void setUserState(Long chatId, BotStates state) {
        userStates.put(chatId, state);
    }

    public void removeUserState(Long chatId) {
        userStates.remove(chatId);
    }
}
