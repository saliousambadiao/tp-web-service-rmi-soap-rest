/**
 * Service web SOAP pour une salle de chat (version ultra-simplifiée)
 * Implémente les fonctionnalités de base d'une salle de discussion avec des types primitifs uniquement
 *
 * Author: Saliou Samba DIAO
 * Email : saliousambadiao@esp.sn
 * Date  : 2025-05-11
 */
public class ChatRoom {
    // Stockage simplié des utilisateurs et messages (maximum 100)
    private static String[] usernames = new String[100];
    private static long[] userTimestamps = new long[100];
    private static int userCount = 0;
    
    private static long[] messageIds = new long[1000];
    private static String[] messageSenders = new String[1000];
    private static String[] messageContents = new String[1000];
    private static String[] messageTimestamps = new String[1000];
    private static int messageCount = 0;
    
    // Pas de timeout pour les utilisateurs - ils se déconnectent manuellement
    
    /**
     * Permet à un utilisateur de se connecter au chat
     * @param username Le pseudo de l'utilisateur
     * @return true si la connexion est réussie, false sinon
     */
    public boolean login(String username) {
        // Vérifier si le pseudo n'est pas vide et n'est pas déjà utilisé
        if (username == null || username.trim().isEmpty() || username.equals("SYSTEM")) {
            return false;
        }
        
        // Vérifier si l'utilisateur est déjà connecté
        for (int i = 0; i < userCount; i++) {
            if (username.equals(usernames[i])) {
                // Mise à jour du timestamp pour prolonger la session
                userTimestamps[i] = System.currentTimeMillis();
                return true;
            }
        }
        
        // Vérifier si la capacité est atteinte
        if (userCount >= 100) {
            return false;
        }
        
        // Ajouter l'utilisateur à la liste des connectés
        usernames[userCount] = username;
        userTimestamps[userCount] = System.currentTimeMillis();
        userCount++;
        
        // Ajouter un message système de connexion
        addSystemMessage(username + " s'est connecté(e)");
        return true;
    }
    
    /**
     * Permet à un utilisateur de se déconnecter du chat
     * @param username Le pseudo de l'utilisateur
     * @return true si la déconnexion est réussie, false sinon
     */
    public boolean logout(String username) {
        // Trouver l'utilisateur et le supprimer
        for (int i = 0; i < userCount; i++) {
            if (username.equals(usernames[i])) {
                // Décaler tous les utilisateurs pour combler le trou
                for (int j = i; j < userCount - 1; j++) {
                    usernames[j] = usernames[j + 1];
                    userTimestamps[j] = userTimestamps[j + 1];
                }
                userCount--;
                
                // Ajouter un message système de déconnexion
                addSystemMessage(username + " s'est déconnecté(e)");
                return true;
            }
        }
        return false;
    }
    
    /**
     * Permet d'envoyer un message dans le chat
     * @param username Le pseudo de l'expéditeur
     * @param content Le contenu du message
     * @return true si l'envoi est réussi, false sinon
     */
    public boolean sendMessage(String username, String content) {
        // Vérifier si l'utilisateur est connecté
        boolean userFound = false;
        for (int i = 0; i < userCount; i++) {
            if (username.equals(usernames[i])) {
                // Mettre à jour le timestamp de dernière activité
                userTimestamps[i] = System.currentTimeMillis();
                userFound = true;
                break;
            }
        }
        
        if (!userFound) {
            return false;
        }
        
        // Vérifier si le message est vide
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        
        // Vérifier si la capacité des messages est atteinte
        if (messageCount >= 1000) {
            // Supprimer les 100 plus anciens messages pour faire de la place
            for (int i = 0; i < messageCount - 100; i++) {
                messageIds[i] = messageIds[i + 100];
                messageSenders[i] = messageSenders[i + 100];
                messageContents[i] = messageContents[i + 100];
                messageTimestamps[i] = messageTimestamps[i + 100];
            }
            messageCount -= 100;
        }
        
        // Ajouter le message
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
        String time = sdf.format(new java.util.Date());
        
        messageIds[messageCount] = System.currentTimeMillis(); // Utiliser le timestamp comme ID
        messageSenders[messageCount] = username;
        messageContents[messageCount] = content;
        messageTimestamps[messageCount] = time;
        messageCount++;
        
        return true;
    }
    
    /**
     * Récupère les ID des nouveaux messages
     * @param username L'utilisateur qui demande les messages
     * @param lastId Dernier ID connu
     * @return Tableau d'ID de nouveaux messages
     */
    public long[] getNewMessageIds(String username, long lastId) {
        // Vérifier si l'utilisateur est connecté
        boolean userFound = false;
        for (int i = 0; i < userCount; i++) {
            if (username.equals(usernames[i])) {
                userTimestamps[i] = System.currentTimeMillis();
                userFound = true;
                break;
            }
        }
        
        if (!userFound) {
            return new long[0];
        }
        
        // Les utilisateurs se déconnectent manuellement - pas de nettoyage automatique
        
        // Compter les nouveaux messages
        int newCount = 0;
        for (int i = 0; i < messageCount; i++) {
            if (messageIds[i] > lastId) {
                newCount++;
            }
        }
        
        // Créer le tableau de résultats
        long[] result = new long[newCount];
        int resultIndex = 0;
        
        for (int i = 0; i < messageCount; i++) {
            if (messageIds[i] > lastId) {
                result[resultIndex++] = messageIds[i];
            }
        }
        
        return result;
    }
    
    /**
     * Récupère le contenu d'un message spécifique
     * @param username L'utilisateur qui fait la demande
     * @param messageId L'ID du message
     * @return Le contenu du message ou "" si non trouvé
     */
    public String getMessageContent(String username, long messageId) {
        // Vérifier si l'utilisateur est connecté
        boolean userFound = false;
        for (int i = 0; i < userCount; i++) {
            if (username.equals(usernames[i])) {
                userTimestamps[i] = System.currentTimeMillis();
                userFound = true;
                break;
            }
        }
        
        if (!userFound) {
            return "";
        }
        
        // Trouver le message
        for (int i = 0; i < messageCount; i++) {
            if (messageIds[i] == messageId) {
                return messageContents[i];
            }
        }
        
        return "";
    }
    
    /**
     * Récupère l'expéditeur d'un message spécifique
     * @param username L'utilisateur qui fait la demande
     * @param messageId L'ID du message
     * @return L'expéditeur du message ou "" si non trouvé
     */
    public String getMessageSender(String username, long messageId) {
        // Vérifier si l'utilisateur est connecté
        boolean userFound = false;
        for (int i = 0; i < userCount; i++) {
            if (username.equals(usernames[i])) {
                userTimestamps[i] = System.currentTimeMillis();
                userFound = true;
                break;
            }
        }
        
        if (!userFound) {
            return "";
        }
        
        // Trouver le message
        for (int i = 0; i < messageCount; i++) {
            if (messageIds[i] == messageId) {
                return messageSenders[i];
            }
        }
        
        return "";
    }
    
    /**
     * Récupère l'horodatage d'un message spécifique
     * @param username L'utilisateur qui fait la demande
     * @param messageId L'ID du message
     * @return L'horodatage du message ou "" si non trouvé
     */
    public String getMessageTimestamp(String username, long messageId) {
        // Vérifier si l'utilisateur est connecté
        boolean userFound = false;
        for (int i = 0; i < userCount; i++) {
            if (username.equals(usernames[i])) {
                userTimestamps[i] = System.currentTimeMillis();
                userFound = true;
                break;
            }
        }
        
        if (!userFound) {
            return "";
        }
        
        // Trouver le message
        for (int i = 0; i < messageCount; i++) {
            if (messageIds[i] == messageId) {
                return messageTimestamps[i];
            }
        }
        
        return "";
    }
    
    /**
     * Récupère la liste des utilisateurs connectés
     * @param username L'utilisateur qui fait la demande
     * @return Un tableau des noms d'utilisateurs connectés
     */
    public String[] getConnectedUsers(String username) {
        // Vérifier si l'utilisateur est connecté
        boolean userFound = false;
        for (int i = 0; i < userCount; i++) {
            if (username.equals(usernames[i])) {
                userTimestamps[i] = System.currentTimeMillis();
                userFound = true;
                break;
            }
        }
        
        if (!userFound) {
            return new String[0];
        }
        
        // Les utilisateurs se déconnectent manuellement - pas de nettoyage automatique
        
        // Créer le tableau de résultats
        String[] result = new String[userCount];
        for (int i = 0; i < userCount; i++) {
            result[i] = usernames[i];
        }
        
        return result;
    }
    
    // Méthode cleanupSessions supprimée - Les utilisateurs se déconnectent désormais manuellement
    
    /**
     * Ajoute un message système au chat
     * @param content Le contenu du message système
     */
    private void addSystemMessage(String content) {
        // Vérifier si la capacité des messages est atteinte
        if (messageCount >= 1000) {
            // Supprimer les 100 plus anciens messages pour faire de la place
            for (int i = 0; i < messageCount - 100; i++) {
                messageIds[i] = messageIds[i + 100];
                messageSenders[i] = messageSenders[i + 100];
                messageContents[i] = messageContents[i + 100];
                messageTimestamps[i] = messageTimestamps[i + 100];
            }
            messageCount -= 100;
        }
        
        // Ajouter le message système
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
        String time = sdf.format(new java.util.Date());
        
        messageIds[messageCount] = System.currentTimeMillis(); // Utiliser le timestamp comme ID
        messageSenders[messageCount] = "SYSTEM";
        messageContents[messageCount] = content;
        messageTimestamps[messageCount] = time;
        messageCount++;
    }
}
