/**
 * Author: Saliou Samba DIAO
 * Email : saliousambadiao@esp.sn
 * Date  : 2025-05-11
 */
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Client de chat avec interface graphique utilisant l'API REST
 */
public class ChatGuiClient {
    private static final String BASE_URL = "http://localhost:8080/chat-rest/api/chat";
    private String username;
    private long lastMessageId = 0;
    private boolean connected = false;
    private Timer timer;
    
    // Composants de l'interface
    private JFrame frame;
    private JTextPane txtOutput;
    private JTextField txtMessage;
    private JButton btnSend;
    private JButton btnDisconnect;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    
    // Styles pour les messages
    private Style systemStyle;
    private Style userStyle;
    private Style currentUserStyle;
    private Style defaultStyle;
    
    // Format de date
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ChatGuiClient().initialize();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Erreur lors de l'initialisation: " + e.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Initialise l'interface graphique
     */
    private void initialize() {
        // Création de la fenêtre principale
        frame = new JFrame("Chat REST Client");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(700, 500);
        
        // Panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Zone d'affichage des messages
        txtOutput = new JTextPane();
        txtOutput.setEditable(false);
        txtOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtOutput);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Initialisation des styles
        initStyles();
        
        // Liste des utilisateurs
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setPreferredSize(new Dimension(150, 400));
        userList.setBorder(BorderFactory.createTitledBorder("Utilisateurs"));
        mainPanel.add(userList, BorderLayout.EAST);
        
        // Panneau d'envoi de messages
        JPanel sendPanel = new JPanel(new BorderLayout(5, 5));
        txtMessage = new JTextField();
        btnSend = new JButton("Envoyer");
        btnSend.setEnabled(false);
        
        // Panneau de boutons
        JPanel buttonPanel = new JPanel(new BorderLayout());
        btnDisconnect = new JButton("Déconnexion");
        btnDisconnect.setEnabled(false);
        buttonPanel.add(btnSend, BorderLayout.CENTER);
        buttonPanel.add(btnDisconnect, BorderLayout.EAST);
        
        sendPanel.add(txtMessage, BorderLayout.CENTER);
        sendPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(sendPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        
        // Configuration des événements
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
            }
        });
        
        btnSend.addActionListener(e -> sendMessage());
        
        btnDisconnect.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                frame, 
                "Voulez-vous vraiment vous déconnecter ?", 
                "Confirmation", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                disconnect();
            }
        });
        
        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        // Affichage de la fenêtre
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Demande du pseudo
        requestUsername();
    }
    
    /**
     * Initialise les styles pour les différents types de messages
     */
    private void initStyles() {
        StyledDocument doc = txtOutput.getStyledDocument();
        
        // Style par défaut
        defaultStyle = doc.addStyle("defaultStyle", null);
        StyleConstants.setForeground(defaultStyle, Color.BLACK);
        
        // Style pour les messages système
        systemStyle = doc.addStyle("systemStyle", null);
        StyleConstants.setForeground(systemStyle, Color.decode("#dc143c")); // Rouge cramoisi
        StyleConstants.setBold(systemStyle, true);
        
        // Style pour l'utilisateur courant
        currentUserStyle = doc.addStyle("currentUserStyle", null);
        StyleConstants.setForeground(currentUserStyle, Color.decode("#008000")); // Vert
        StyleConstants.setBold(currentUserStyle, true);
        
        // Style pour les autres utilisateurs
        userStyle = doc.addStyle("userStyle", null);
        StyleConstants.setForeground(userStyle, Color.decode("#073e18")); // Vert foncé
    }
    
    /**
     * Affiche un message avec le style approprié
     */
    private void displayStyledMessage(ChatMessage msg) {
        StyledDocument doc = txtOutput.getStyledDocument();
        Style style;
        String formattedMessage;
        
        if ("SYSTEM".equals(msg.getSender())) {
            style = systemStyle;
            formattedMessage = "SYSTEM => [" + msg.getTimestamp() + "] ** " + msg.getContent() + " **\n";
        } else if (username.equals(msg.getSender())) {
            style = currentUserStyle;
            formattedMessage = msg.getSender() + " => [" + msg.getTimestamp() + "] : " + msg.getContent() + "\n";
        } else {
            style = userStyle;
            formattedMessage = msg.getSender() + " => [" + msg.getTimestamp() + "] : " + msg.getContent() + "\n";
        }
        
        try {
            doc.insertString(doc.getLength(), formattedMessage, style);
        } catch (BadLocationException e) {
            // Fallback en cas d'erreur
            System.err.println("Erreur d'affichage: " + e.getMessage());
        }
    }
    
    /**
     * Demande le pseudo à l'utilisateur
     */
    private void requestUsername() {
        username = JOptionPane.showInputDialog(
            frame, 
            "Entrez votre pseudo :", 
            "Connexion", 
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (username == null || username.trim().isEmpty()) {
            System.exit(0);
        }
        
        username = username.trim();
        if (login()) {
            connected = true;
            btnSend.setEnabled(true);
            btnDisconnect.setEnabled(true);
            frame.setTitle("Chat REST Client - " + username);
            startPolling();
            txtMessage.requestFocus();
        } else {
            JOptionPane.showMessageDialog(
                frame, 
                "Impossible de se connecter au serveur.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
    }
    
    /**
     * Envoie un message
     */
    private void sendMessage() {
        String message = txtMessage.getText().trim();
        if (!message.isEmpty() && connected) {
            if (postMessage(message)) {
                txtMessage.setText("");
                txtMessage.requestFocus();
            } else {
                JOptionPane.showMessageDialog(
                    frame, 
                    "Impossible d'envoyer le message.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * Déconnecte l'utilisateur et ferme l'application
     */
    private void disconnect() {
        if (connected) {
            if (logout()) {
                JOptionPane.showMessageDialog(
                    frame, 
                    "Vous avez été déconnecté avec succès.", 
                    "Déconnexion", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
            
            if (timer != null) {
                timer.cancel();
            }
            
            frame.dispose();
            System.exit(0);
        } else {
            frame.dispose();
            System.exit(0);
        }
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
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Envoie d'un message au serveur
     */
    private boolean postMessage(String content) {
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
            return responseCode == 200;
        } catch (Exception e) {
            e.printStackTrace();
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
                List<ChatMessage> messages = parseMessages(jsonResponse);
                
                // Traitement des nouveaux messages
                SwingUtilities.invokeLater(() -> {
                    for (ChatMessage msg : messages) {
                        if (msg.getId() > lastMessageId) {
                            lastMessageId = msg.getId();
                            
                            // Affichage du message avec style
                            displayStyledMessage(msg);
                            
                            // Défilement automatique
                            txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
                        }
                    }
                });
            }
        } catch (Exception e) {
            // Ignorer silencieusement les erreurs de polling
        }
    }
    
    /**
     * Récupération de la liste des utilisateurs
     */
    private void fetchActiveUsers() {
        try {
            URL url = new URL(BASE_URL + "/users");
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
                List<String> users = parseUsers(jsonResponse);
                
                // Mise à jour de la liste des utilisateurs
                SwingUtilities.invokeLater(() -> {
                    userListModel.clear();
                    for (String user : users) {
                        userListModel.addElement(user);
                    }
                });
            }
        } catch (Exception e) {
            // Ignorer silencieusement les erreurs de polling
        }
    }
    
    /**
     * Démarrer le polling des messages et des utilisateurs
     */
    private void startPolling() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (connected) {
                    fetchNewMessages();
                    fetchActiveUsers();
                }
            }
        }, 0, 1000); // Polling chaque seconde
    }
    
    /**
     * Analyse les messages JSON (version simplifiée)
     */
    private List<ChatMessage> parseMessages(String json) {
        List<ChatMessage> messages = new ArrayList<>();
        
        // Parsing très basique - une implémentation réelle utiliserait Jackson/Gson
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1);
            if (json.trim().isEmpty()) {
                return messages;
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
                    parseAndAddMessage(messageJson, messages);
                    start = i + 1;
                }
            }
        }
        
        return messages;
    }
    
    /**
     * Analyse un message JSON individuel
     */
    private void parseAndAddMessage(String messageJson, List<ChatMessage> messages) {
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
            
            String timestamp = timeFormat.format(new Date());
            ChatMessage message = new ChatMessage(id, sender, content, timestamp);
            messages.add(message);
        } catch (Exception e) {
            // Ignorer les erreurs de parsing
        }
    }
    
    /**
     * Analyse la liste des utilisateurs JSON (version simplifiée)
     */
    private List<String> parseUsers(String json) {
        List<String> users = new ArrayList<>();
        
        // Parsing très basique - une implémentation réelle utiliserait Jackson/Gson
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1);
            if (json.trim().isEmpty()) {
                return users;
            }
            
            // Découper en éléments
            String[] elements = json.split(",");
            for (String element : elements) {
                element = element.trim();
                if (element.startsWith("\"") && element.endsWith("\"")) {
                    element = element.substring(1, element.length() - 1);
                    users.add(element);
                }
            }
        }
        
        return users;
    }
    
    /**
     * Classe interne représentant un message
     */
    private static class ChatMessage {
        private long id;
        private String sender;
        private String content;
        private String timestamp;
        
        public ChatMessage(long id, String sender, String content, String timestamp) {
            this.id = id;
            this.sender = sender;
            this.content = content;
            this.timestamp = timestamp;
        }
        
        public long getId() {
            return id;
        }
        
        public String getSender() {
            return sender;
        }
        
        public String getContent() {
            return content;
        }
        
        public String getTimestamp() {
            return timestamp;
        }
    }
}
