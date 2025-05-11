import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Client de chat en ligne de commande utilisant l'API REST
 */
public class ChatConsoleClient {
    private static final String BASE_URL = "http://localhost:8080/chat-rest/api/chat";
    private String username;
    private long lastMessageId = 0;
    private boolean running = true;
    private Timer timer;

    public static void main(String[] args) {
        ChatConsoleClient client = new ChatConsoleClient();
        client.start();
    }

    /**
     * Démarre le client de chat
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Client de Chat REST ===");
        System.out.print("Entrez votre pseudo: ");
        username = scanner.nextLine().trim();
        
        if (username.isEmpty()) {
            System.out.println("Le pseudo ne peut pas être vide.");
            return;
        }
        
        // Connexion au serveur
        if (!login()) {
            System.out.println("Impossible de se connecter au serveur.");
            return;
        }
        
        System.out.println("Connecté en tant que " + username);
        System.out.println("Tapez 'quit' pour quitter");
        
        // Démarrer le polling des messages
        startPolling();
        
        // Boucle principale pour l'entrée des messages
        while (running) {
            String input = scanner.nextLine();
            
            if ("quit".equalsIgnoreCase(input.trim())) {
                logout();
                running = false;
                if (timer != null) {
                    timer.cancel();
                }
                System.out.println("Déconnecté du serveur. Au revoir!");
                break;
            }
            
            if (!input.trim().isEmpty()) {
                sendMessage(input);
            }
        }
        
        scanner.close();
    }
    
    /**
     * Connexion au serveur
     */
    private boolean login() {
        try {
            URL url = new URL(BASE_URL + "/users");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            String data = "username=" + URLEncoder.encode(username, StandardCharsets.UTF_8.toString());
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
            }
            
            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Déconnexion du serveur
     */
    private boolean logout() {
        try {
            URL url = new URL(BASE_URL + "/users/" + URLEncoder.encode(username, StandardCharsets.UTF_8.toString()));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            
            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Envoie d'un message
     */
    private boolean sendMessage(String content) {
        try {
            URL url = new URL(BASE_URL + "/messages");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            String data = "username=" + URLEncoder.encode(username, StandardCharsets.UTF_8.toString()) +
                          "&content=" + URLEncoder.encode(content, StandardCharsets.UTF_8.toString());
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine = in.readLine();
                in.close();
                
                try {
                    long messageId = Long.parseLong(inputLine.trim());
                    if (messageId > lastMessageId) {
                        lastMessageId = messageId;
                    }
                } catch (NumberFormatException e) {
                    // Ignorer les erreurs de parsing
                }
                
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du message: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupération des nouveaux messages
     */
    private void fetchNewMessages() {
        try {
            URL url = new URL(BASE_URL + "/messages?since=" + lastMessageId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                String jsonResponse = response.toString();
                // Parsing simplifié des messages
                // Dans un cas réel, nous utiliserions Jackson ou Gson
                parseAndDisplayMessages(jsonResponse);
            }
        } catch (Exception e) {
            // Ignorer silencieusement les erreurs de polling
        }
    }
    
    /**
     * Analyser les messages JSON et les afficher
     * Version simplifiée sans bibliothèque de parsing JSON
     */
    private void parseAndDisplayMessages(String json) {
        // Parsing très basique - une implémentation réelle utiliserait Jackson/Gson
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1);
            if (json.trim().isEmpty()) {
                return;
            }
            
            // Séparer les objets JSON
            int depth = 0;
            int start = 0;
            
            for (int i = 0; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == '{') depth++;
                else if (c == '}') depth--;
                
                if (depth == 0 && (c == ',' || i == json.length() - 1)) {
                    String messageJson = json.substring(start, i == json.length() - 1 ? i + 1 : i);
                    displayMessage(messageJson);
                    start = i + 1;
                }
            }
        }
    }
    
    /**
     * Afficher un message individuel
     */
    private void displayMessage(String messageJson) {
        try {
            long id = 0;
            String sender = "";
            String content = "";
            
            // Extraction très basique des champs JSON
            if (messageJson.contains("\"id\":")) {
                String idStr = messageJson.split("\"id\":")[1].split(",")[0].trim();
                id = Long.parseLong(idStr);
            }
            
            if (messageJson.contains("\"sender\":")) {
                sender = messageJson.split("\"sender\":")[1].split(",")[0].trim();
                if (sender.startsWith("\"") && sender.endsWith("\"")) {
                    sender = sender.substring(1, sender.length() - 1);
                }
            }
            
            if (messageJson.contains("\"content\":")) {
                content = messageJson.split("\"content\":")[1].split(",")[0].trim();
                if (content.startsWith("\"") && content.endsWith("\"")) {
                    content = content.substring(1, content.length() - 1);
                }
                // Gestion des fins d'objets
                if (content.endsWith("}")) {
                    content = content.substring(0, content.length() - 1);
                }
            }
            
            if (id > lastMessageId) {
                lastMessageId = id;
                
                // Mettre en forme et afficher le message
                if ("SYSTEM".equals(sender)) {
                    System.out.println("[SYSTEM] " + content);
                } else {
                    System.out.println(sender + ": " + content);
                }
            }
        } catch (Exception e) {
            // Ignorer les erreurs de parsing
        }
    }
    
    /**
     * Démarrer le polling des messages
     */
    private void startPolling() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (running) {
                    fetchNewMessages();
                }
            }
        }, 0, 1000); // Polling chaque seconde
    }
}
