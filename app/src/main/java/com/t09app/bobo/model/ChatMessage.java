package com.t09app.bobo.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    private final String message;
    private final boolean isBot;
    private final String timestamp;
    private boolean isAnimated = false;

    public ChatMessage(String message, boolean isBot) {
        this.message = message;
        this.isBot = isBot;
        this.timestamp = getCurrentTimestamp();
    }

    public String getMessage() {
        return message;
    }

    public boolean isBot() {
        return isBot;
    }

    public String getTimestamp() {
        return timestamp;
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public void setAnimated(boolean animated) {
        isAnimated = animated;
    }
}
