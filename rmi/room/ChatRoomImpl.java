/**
 * Author: Saliou Samba DIAO
 * Email : saliousambadiao@esp.sn
 * Date  : 2025-05-11
 */
package room;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Hashtable;

import user.ChatUser;

@SuppressWarnings("")
public class ChatRoomImpl extends UnicastRemoteObject implements ChatRoom {
    
    Hashtable<String, ChatUser> users;

    public ChatRoomImpl() throws RemoteException
	{
		super();
		users = new Hashtable<String, ChatUser>();
	}

    @Override
    public void subscribe(ChatUser user, String pseudo) throws RemoteException
    {
        if (!users.containsKey(pseudo))
		{
			// D'abord ajouter l'utilisateur à la liste
			users.put(pseudo, user);
			
			// Ensuite envoyer directement la notification de connexion sans passer par postMessage
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
			String time = sdf.format(new java.util.Date());
			String systemMessage = "SYSTEM => [" + time + "] ** " + pseudo + " s'est connecté(e) **";
			
			// Distribuer directement le message aux utilisateurs connectés
			Enumeration<ChatUser> e = users.elements();
			while (e.hasMoreElements())
			{
				ChatUser u = e.nextElement();
				u.displayMessage(systemMessage);
			}
		}
    }

    @Override
    public void unsubscribe(String pseudo) throws RemoteException
    {
        if (users.containsKey(pseudo))
		{
			// D'abord supprimer l'utilisateur de la liste
			users.remove(pseudo);
			
			// Ensuite envoyer directement la notification de déconnexion sans passer par postMessage
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
			String time = sdf.format(new java.util.Date());
			String systemMessage = "SYSTEM => [" + time + "] ** " + pseudo + " s'est déconnecté(e) **";
			
			// Distribuer directement le message aux utilisateurs encore connectés
			Enumeration<ChatUser> e = users.elements();
			while (e.hasMoreElements())
			{
				ChatUser u = e.nextElement();
				u.displayMessage(systemMessage);
			}
		}
    }


    @Override
    public void postMessage(String pseudo, String message) throws RemoteException
    {
        // Vérifier si le message est vide et que ce n'est pas un message système
        if (!"SYSTEM".equals(pseudo) && (message == null || message.trim().isEmpty())) {
            // Ne pas traiter les messages vides qui ne sont pas du système
            return;
        }
        
        // Ajouter l'horodatage au message
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
        String time = sdf.format(new java.util.Date());
        String timeStampedMessage;
        
        // Vérifier si c'est un message système (déjà formaté avec l'heure)
        if ("SYSTEM".equals(pseudo) && message.contains("[") && message.contains("]")) {
            // Pour les messages système, conserver le format existant
            timeStampedMessage = message;
        } else {
            // Pour les messages des utilisateurs, ajouter l'horodatage
            timeStampedMessage = "[" + time + "] " + message;
        }
        
        String fullMessage = pseudo + " => " + timeStampedMessage;
        Enumeration<ChatUser> e = users.elements();
        while (e.hasMoreElements())
		{
			ChatUser user = (ChatUser) e.nextElement();
			user.displayMessage(fullMessage);
		}
    }
}
