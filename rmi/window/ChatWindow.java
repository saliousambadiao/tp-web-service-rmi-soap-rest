/**
 * Author: Saliou Samba DIAO
 * Email : saliousambadiao@esp.sn
 * Date  : 2025-05-11
 */
package window;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.*;
import javax.swing.text.*;

import room.ChatRoom;
import user.ChatUserImpl;

public class ChatWindow implements Serializable{

    private String title = "Logiciel de discussion en ligne";
    private String pseudo = null;
    private ChatRoom room = null;

    private JFrame window = new JFrame(this.title);
    private JTextPane txtOutput = new JTextPane();
    private JTextField txtMessage = new JTextField();
    private JButton btnSend = new JButton("Envoyer");
    private JButton btnLogout = new JButton("Déconnexion");
    ChatUserImpl chatUserImpl;

    public ChatWindow() throws RemoteException
    {
        this.createIHM();
        try
		{
			chatUserImpl = new ChatUserImpl();
			chatUserImpl.setIg(this);
			
			// obtention d'une référence sur l'objet distant à partir de son nom
			// Changer le paramètre de la fonction lookup en fonction de votre utilisation
			// (@IP du serveur)
			Remote r = Naming.lookup("rmi://localhost:1099/CHAT");
			this.room = (ChatRoom) r;
			this.requestPseudo();
		} catch (MalformedURLException e)
		{
			System.out.println("Impossible de joindre le salon de discussion...");
			System.exit(0);
			e.printStackTrace();
		} catch (RemoteException e)
		{
			e.printStackTrace();
		} catch (NotBoundException e)
		{
			e.printStackTrace();
		}
    }

    public void createIHM() 
    {
        // Assemblage des composants
        JPanel panel = (JPanel)this.window.getContentPane();
	    JScrollPane sclPane = new JScrollPane(txtOutput);
	    panel.add(sclPane, BorderLayout.CENTER);
        
        // Panneau sud avec champ de texte, bouton d'envoi et bouton de déconnexion
        JPanel southPanel = new JPanel(new BorderLayout());
        
        // Sous-panneau pour les boutons
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(this.btnSend, BorderLayout.CENTER);
        buttonPanel.add(this.btnLogout, BorderLayout.EAST);
        
        southPanel.add(this.txtMessage, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(southPanel, BorderLayout.SOUTH);

        // Gestion des évènements
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try
				{
					window_windowClosing(e);
				} catch (RemoteException e1)
				{
					e1.printStackTrace();
				}
            }
        });
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnSend_actionPerformed(e);
            }
        });
        
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    btnLogout_actionPerformed(e);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
	    txtMessage.addKeyListener(new KeyAdapter() {
	    public void keyReleased(KeyEvent event) {
		if (event.getKeyChar() == '\n')
		    btnSend_actionPerformed(null);
	    }
	    });

        // Initialisation des attributs
        this.txtOutput.setBackground(new Color(220,220,220));
	    this.txtOutput.setEditable(false);
        this.window.setSize(500,400);
        this.window.setVisible(true);
        this.txtMessage.requestFocus();
    }

    public void requestPseudo() throws RemoteException
    {
        this.pseudo = JOptionPane.showInputDialog(
                this.window, "Entrez votre pseudo : ",
                this.title,  JOptionPane.OK_OPTION
        );
        if (this.pseudo == null) System.exit(0);
        this.room.subscribe(chatUserImpl, this.pseudo);
    }

    public void window_windowClosing(WindowEvent e) throws RemoteException
    {
        this.room.unsubscribe(this.pseudo);
	    System.exit(0);
    }
    
    /**
     * Gère l'action du bouton de déconnexion
     */
    public void btnLogout_actionPerformed(ActionEvent e) throws RemoteException
    {
        // Demande de confirmation
        int choice = JOptionPane.showConfirmDialog(
            window,
            "Voulez-vous vraiment vous déconnecter ?",
            "Confirmation de déconnexion",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            // Se déconnecter du chat
            this.room.unsubscribe(this.pseudo);
            
            // Afficher un message de confirmation
            JOptionPane.showMessageDialog(
                window,
                "Vous avez été déconnecté avec succès.",
                "Déconnexion réussie",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Fermer l'application
            System.exit(0);
        }
    }

    public void btnSend_actionPerformed(ActionEvent e) 
    {
        try
		{
			this.room.postMessage(this.pseudo, this.txtMessage.getText());
		} catch (RemoteException e1)
		{
			e1.printStackTrace();
			System.out.println("Impossible d'envoyer le message...");
		}
		this.txtMessage.setText("");
		this.txtMessage.requestFocus();
    }

    public void displayMessage(String message) throws RemoteException
    {
        // S'il n'y a pas de message, ne rien faire
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        
        // Format du message : "pseudo => contenu"
        String[] parts = message.split(" => ", 2);
        
        Color backgroundColor;
        String displayMessage;
        
        if (parts.length == 2) {
            String sender = parts[0];
            String content = parts[1];
            
            // Vérifier si le contenu est vide
            if (content.trim().isEmpty() && !"SYSTEM".equals(sender)) {
                // Ne pas afficher les messages vides qui ne sont pas du système
                return;
            }
            
            displayMessage = sender + " => " + content;
            
            // Déterminer le type de message
            if ("SYSTEM".equals(sender)) {
                // Messages système - rouge cramoisi
                backgroundColor = Color.decode("#dc143c");
            } else if (this.pseudo.equals(sender)) {
                // Mes propres messages - vert
                backgroundColor = Color.decode("#008000");
            } else {
                // Messages des autres - vert foncé
                backgroundColor = Color.decode("#073e18");
            }
        } else {
            // Message avec format incorrect - utiliser noir
            displayMessage = message;
            backgroundColor = Color.BLACK;
        }
        
        // Ajouter au document avec la couleur appropriée
        appendToPane(displayMessage + "\n", backgroundColor);
    }
    
    /**
     * Méthode utilitaire pour ajouter du texte coloré au JTextPane
     */
    private void appendToPane(String message, Color color) {
        StyledDocument doc = txtOutput.getStyledDocument();
        Style style = txtOutput.addStyle("Color Style", null);
        
        // Utiliser la couleur pour le fond et non pour le texte
        StyleConstants.setBackground(style, color);
        // Mettre le texte en blanc pour qu'il soit lisible sur fond coloré
        StyleConstants.setForeground(style, Color.WHITE);
        
        try {
            doc.insertString(doc.getLength(), message, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        
        // Auto-scroll vers le bas
        txtOutput.setCaretPosition(doc.getLength());
    }

    public static void main(String[] args) throws RemoteException
    {
        new ChatWindow();
    }
}
