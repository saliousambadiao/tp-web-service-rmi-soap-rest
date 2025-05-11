package com.chat.service;

import com.chat.model.Message;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service gérant les utilisateurs et les messages de chat
 */
public class ChatService {
    // Map des utilisateurs actifs avec leur dernier timestamp d'activité
    private Map<String, Long> activeUsers = new ConcurrentHashMap<>();
    
    // Liste des messages (thread-safe)
    private List<Message> messages = Collections.synchronizedList(new ArrayList<>());
    
    // Générateur d'ID pour les messages
    private AtomicLong messageIdGenerator = new AtomicLong(0);
    
    // Nombre maximum de messages à conserver
    private static final int MAX_MESSAGES = 100;
    
    /**
     * Enregistre un nouvel utilisateur
     * @param username Le nom d'utilisateur
     * @return true si l'utilisateur a été ajouté, false sinon
     */
    public boolean registerUser(String username) {
        if (username == null || username.trim().isEmpty() || username.equals("SYSTEM")) {
            return false;
        }
        
        // Ajouter l'utilisateur s'il n'existe pas déjà
        if (!activeUsers.containsKey(username)) {
            activeUsers.put(username, System.currentTimeMillis());
            
            // Annoncer l'arrivée de l'utilisateur
            Message systemMessage = new Message(
                messageIdGenerator.incrementAndGet(),
                "SYSTEM",
                "L'utilisateur " + username + " a rejoint le chat."
            );
            messages.add(systemMessage);
            
            // Nettoyer les vieux messages si nécessaire
            cleanupOldMessages();
            
            return true;
        }
        return false;
    }
    
    /**
     * Supprime un utilisateur du chat
     * @param username Le nom d'utilisateur à supprimer
     * @return true si l'utilisateur a été supprimé, false sinon
     */
    public boolean removeUser(String username) {
        if (activeUsers.remove(username) != null) {
            // Annoncer le départ de l'utilisateur
            Message systemMessage = new Message(
                messageIdGenerator.incrementAndGet(),
                "SYSTEM",
                "L'utilisateur " + username + " a quitté le chat."
            );
            messages.add(systemMessage);
            
            // Nettoyer les vieux messages si nécessaire
            cleanupOldMessages();
            
            return true;
        }
        return false;
    }
    
    /**
     * Renvoie la liste des utilisateurs actifs
     * @return Liste des noms d'utilisateurs
     */
    public List<String> getActiveUsers() {
        return new ArrayList<>(activeUsers.keySet());
    }
    
    /**
     * Publie un nouveau message
     * @param username L'expéditeur du message
     * @param content Le contenu du message
     * @return L'ID du message créé
     */
    public long postMessage(String username, String content) {
        // Vérifier si l'utilisateur existe et si le contenu n'est pas vide
        if (!activeUsers.containsKey(username) || content == null || content.trim().isEmpty()) {
            return -1;
        }
        
        // Mettre à jour le timestamp de l'utilisateur
        activeUsers.put(username, System.currentTimeMillis());
        
        // Créer et ajouter le message
        long id = messageIdGenerator.incrementAndGet();
        Message message = new Message(id, username, content);
        messages.add(message);
        
        // Nettoyer les vieux messages si nécessaire
        cleanupOldMessages();
        
        return id;
    }
    
    /**
     * Récupère tous les messages
     * @return Liste de tous les messages
     */
    public List<Message> getAllMessages() {
        return new ArrayList<>(messages);
    }
    
    /**
     * Récupère les messages après un certain ID
     * @param messageId L'ID à partir duquel récupérer les messages
     * @return Liste des messages après l'ID spécifié
     */
    public List<Message> getMessagesAfter(long messageId) {
        return messages.stream()
                .filter(message -> message.getId() > messageId)
                .collect(Collectors.toList());
    }
    
    /**
     * Supprime les messages les plus anciens si le nombre maximum est dépassé
     */
    private void cleanupOldMessages() {
        if (messages.size() > MAX_MESSAGES) {
            synchronized (messages) {
                if (messages.size() > MAX_MESSAGES) {
                    messages.subList(0, messages.size() - MAX_MESSAGES).clear();
                }
            }
        }
    }
}
