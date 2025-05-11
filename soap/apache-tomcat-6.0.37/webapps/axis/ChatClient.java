/**
 * Author: Saliou Samba DIAO
 * Email : saliousambadiao@esp.sn
 * Date  : 2025-05-11
 */
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import javax.xml.rpc.ParameterMode;
import javax.xml.namespace.QName;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Client SOAP pour le service de chat
 */
public class ChatClient {
    private String title = "Chat SOAP";
    private String username = null;
    private String endpoint = "http://localhost:8080/axis/ChatRoom.jws";
    
    // Variables pour suivre l'état de la session
    private boolean connected = false;
    private long lastMessageId = 0;
    
    // Composants de l'interface graphique
    private JFrame window = new JFrame(this.title);
    private JTextPane txtOutput = new JTextPane();
    private JTextField txtMessage = new JTextField();
    private JButton btnSend = new JButton("Envoyer");
    private JButton btnLogout = new JButton("Déconnexion");
    private JList<String> userList = new JList<>();
    private DefaultListModel<String> userListModel = new DefaultListModel<>();
    
    // Thread pour la récupération des messages
    private MessagePoller messagePoller;
    
    /**
     * Constructeur principal
     */
    public ChatClient() {
        createUI();
        requestUsername();
    }
    
    /**
     * Crée l'interface utilisateur
     */
    private void createUI() {
        // Initialisation de la liste d'utilisateurs
        userList.setModel(userListModel);
        
        // Configuration du panneau de texte
        txtOutput.setBackground(new Color(220, 220, 220));
        txtOutput.setEditable(false);
        
        // Assemblage des composants
        JPanel panel = (JPanel) this.window.getContentPane();
        
        // Panneau divisé avec liste d'utilisateurs à droite
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        
        JScrollPane messageScrollPane = new JScrollPane(txtOutput);
        JScrollPane userScrollPane = new JScrollPane(userList);
        
        splitPane.setLeftComponent(messageScrollPane);
        splitPane.setRightComponent(userScrollPane);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Panneau sud avec champ de texte, bouton d'envoi et bouton de déconnexion
        JPanel southPanel = new JPanel(new BorderLayout());
        
        // Sous-panneau pour les boutons
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(this.btnSend, BorderLayout.CENTER);
        buttonPanel.add(this.btnLogout, BorderLayout.EAST);
        
        southPanel.add(this.txtMessage, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(southPanel, BorderLayout.SOUTH);
        
        // Gestion des événements
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                windowClosing_action(e);
            }
        });
        
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage_action(e);
            }
        });
        
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout_action(e);
            }
        });
        
        txtMessage.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                if (event.getKeyChar() == '\n')
                    sendMessage_action(null);
            }
        });
        
        // Configuration finale de la fenêtre
        this.window.setSize(500, 400);
        this.window.setVisible(true);
        this.txtMessage.requestFocus();
    }
    
    /**
     * Demande à l'utilisateur de saisir son pseudo
     */
    private void requestUsername() {
        this.username = JOptionPane.showInputDialog(
                this.window, "Entrez votre pseudo :",
                this.title, JOptionPane.OK_OPTION
        );
        
        if (this.username == null || this.username.trim().isEmpty()) {
            System.exit(0);
        }
        
        connectToChat();
    }
    
    /**
     * Se connecte au service de chat
     */
    private void connectToChat() {
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(endpoint));
            call.setOperationName("login");
            call.addParameter("username", XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_BOOLEAN);
            
            Boolean result = (Boolean) call.invoke(new Object[] { username });
            
            if (Boolean.TRUE.equals(result)) {
                // Connexion réussie
                connected = true;
                window.setTitle(title + " - " + username);
                
                // Démarrer le thread de polling
                messagePoller = new MessagePoller();
                messagePoller.start();
                
                // Afficher un message de bienvenue
                appendToPane("Bienvenue dans le chat SOAP!\n", Color.BLUE);
            } else {
                // Échec de la connexion
                appendToPane("Échec de la connexion. Le pseudo est peut-être déjà utilisé.\n", Color.RED);
                requestUsername();
            }
        } catch (Exception e) {
            appendToPane("Erreur de connexion au serveur: " + e.getMessage() + "\n", Color.RED);
            e.printStackTrace();
        }
    }
    
    /**
     * Gère la fermeture de la fenêtre
     */
    private void windowClosing_action(WindowEvent e) {
        try {
            if (connected && username != null) {
                // Arrêter le thread de polling
                if (messagePoller != null) {
                    messagePoller.stopPolling();
                }
                
                // Se déconnecter du chat
                Service service = new Service();
                Call call = (Call) service.createCall();
                call.setTargetEndpointAddress(new URL(endpoint));
                call.setOperationName("logout");
                call.addParameter("username", XMLType.XSD_STRING, ParameterMode.IN);
                call.setReturnType(XMLType.XSD_BOOLEAN);
                
                call.invoke(new Object[] { username });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
    
    /**
     * Gère l'action du bouton de déconnexion
     */
    private void logout_action(ActionEvent e) {
        try {
            if (connected && username != null) {
                // Demande de confirmation
                int choice = JOptionPane.showConfirmDialog(
                    window,
                    "Voulez-vous vraiment vous déconnecter ?",
                    "Confirmation de déconnexion",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (choice == JOptionPane.YES_OPTION) {
                    // Arrêter le thread de polling
                    if (messagePoller != null) {
                        messagePoller.stopPolling();
                    }
                    
                    // Se déconnecter du chat
                    Service service = new Service();
                    Call call = (Call) service.createCall();
                    call.setTargetEndpointAddress(new URL(endpoint));
                    call.setOperationName("logout");
                    call.addParameter("username", XMLType.XSD_STRING, ParameterMode.IN);
                    call.setReturnType(XMLType.XSD_BOOLEAN);
                    
                    Boolean result = (Boolean) call.invoke(new Object[] { username });
                    
                    if (Boolean.TRUE.equals(result)) {
                        appendToPane("Vous avez été déconnecté avec succès.\n", Color.RED);
                        JOptionPane.showMessageDialog(window, "Déconnexion réussie", "Information", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(window, "Erreur lors de la déconnexion: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Gère l'envoi de message
     */
    private void sendMessage_action(ActionEvent e) {
        String message = txtMessage.getText().trim();
        if (message.isEmpty() || !connected) {
            return;
        }
        
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(endpoint));
            call.setOperationName("sendMessage");
            call.addParameter("username", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("content", XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_BOOLEAN);
            
            Boolean result = (Boolean) call.invoke(new Object[] { username, message });
            
            if (Boolean.TRUE.equals(result)) {
                txtMessage.setText("");
                txtMessage.requestFocus();
            } else {
                appendToPane("Échec de l'envoi du message.\n", Color.RED);
            }
        } catch (Exception ex) {
            appendToPane("Erreur lors de l'envoi du message: " + ex.getMessage() + "\n", Color.RED);
            ex.printStackTrace();
        }
    }
    
    /**
     * Affiche un message dans la zone de texte
     */
    private void appendToPane(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = txtOutput.getStyledDocument();
            Style style = txtOutput.addStyle("Color Style", null);
            
            StyleConstants.setForeground(style, color);
            
            try {
                doc.insertString(doc.getLength(), message, style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            
            // Auto-scroll vers le bas
            txtOutput.setCaretPosition(doc.getLength());
        });
    }
    
    /**
     * Met à jour la liste des utilisateurs
     */
    private void updateUserList(String[] users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users) {
                userListModel.addElement(user);
            }
        });
    }
    
    /**
     * Classe interne pour le thread de polling
     */
    private class MessagePoller extends Thread {
        private boolean running = true;
        private static final long POLLING_INTERVAL = 1000; // 1 seconde
        
        @Override
        public void run() {
            while (running && connected) {
                try {
                    pollForMessages();
                    pollForUsers();
                    Thread.sleep(POLLING_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        public void stopPolling() {
            running = false;
            this.interrupt();
        }
        
        private void pollForMessages() throws Exception {
            // 1. Récupérer les IDs des nouveaux messages
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(endpoint));
            call.setOperationName("getNewMessageIds");
            call.addParameter("username", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("lastId", XMLType.XSD_LONG, ParameterMode.IN);
            call.setReturnType(XMLType.SOAP_ARRAY);
            
            Object[] result = (Object[]) call.invoke(new Object[] { username, lastMessageId });
            
            if (result != null && result.length > 0) {
                // Pour chaque ID de message, récupérer les détails
                for (Object idObj : result) {
                    try {
                        long msgId = ((Number) idObj).longValue();
                        
                        // Mettre à jour le dernier ID de message
                        if (msgId > lastMessageId) {
                            lastMessageId = msgId;
                        }
                        
                        // Récupérer le contenu du message
                        String content = getMessageContent(msgId);
                        // Récupérer l'expéditeur du message
                        String sender = getMessageSender(msgId);
                        // Récupérer l'horodatage du message
                        String timestamp = getMessageTimestamp(msgId);
                        
                        if (content != null && sender != null && timestamp != null) {
                            // Formater et afficher le message
                            String formattedMsg = "[" + timestamp + "] ";
                            Color msgColor;
                            
                            if ("SYSTEM".equals(sender)) {
                                formattedMsg += "** " + content + " **\n";
                                msgColor = Color.RED;
                            } else {
                                formattedMsg += sender + ": " + content + "\n";
                                // Messages de l'utilisateur courant en bleu, autres en noir
                                msgColor = username.equals(sender) ? Color.BLUE : Color.BLACK;
                            }
                            
                            appendToPane(formattedMsg, msgColor);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        private String getMessageContent(long messageId) throws Exception {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(endpoint));
            call.setOperationName("getMessageContent");
            call.addParameter("username", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("messageId", XMLType.XSD_LONG, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_STRING);
            
            return (String) call.invoke(new Object[] { username, messageId });
        }
        
        private String getMessageSender(long messageId) throws Exception {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(endpoint));
            call.setOperationName("getMessageSender");
            call.addParameter("username", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("messageId", XMLType.XSD_LONG, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_STRING);
            
            return (String) call.invoke(new Object[] { username, messageId });
        }
        
        private String getMessageTimestamp(long messageId) throws Exception {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(endpoint));
            call.setOperationName("getMessageTimestamp");
            call.addParameter("username", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("messageId", XMLType.XSD_LONG, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_STRING);
            
            return (String) call.invoke(new Object[] { username, messageId });
        }
        
        private void pollForUsers() throws Exception {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(endpoint));
            call.setOperationName("getConnectedUsers");
            call.addParameter("username", XMLType.XSD_STRING, ParameterMode.IN);
            
            // Utiliser directement le type tableau SOAP standard
            call.setReturnType(XMLType.SOAP_ARRAY);
            
            Object[] result = (Object[]) call.invoke(new Object[] { username });
            
            if (result != null) {
                String[] users = Arrays.copyOf(result, result.length, String[].class);
                updateUserList(users);
            }
        }
    }
    
    /**
     * Point d'entrée de l'application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatClient();
        });
    }
}
