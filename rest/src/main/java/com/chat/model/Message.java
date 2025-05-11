/**
 * Author: Saliou Samba DIAO
 * Email : saliousambadiao@esp.sn
 * Date  : 2025-05-11
 */
package com.chat.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Représente un message dans le système de chat.
 */
public class Message {
    private long id;
    private String sender;
    private String content;
    private LocalDateTime timestamp;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Constructeur par défaut (nécessaire pour la sérialisation JSON)
     */
    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructeur avec paramètres
     */
    public Message(long id, String sender, String content) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    // Getters et setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Renvoie l'heure formatée du message (HH:mm:ss)
     */
    public String getFormattedTime() {
        return timestamp.format(formatter);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s", getFormattedTime(), sender, content);
    }
}
