package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import room.ChatRoomImpl;

public class ChatServer {
    public static void main(String[] args)
	{
		try
		{
			LocateRegistry.createRegistry(1099);
			ChatRoomImpl chatRoomImpl = new ChatRoomImpl();
			
			Naming.rebind("rmi://localhost/CHAT", chatRoomImpl);
			
			System.out.println("Serveur du ChatRoom démarré, en attente de connexions...");
		} catch (RemoteException e)
		{
			e.printStackTrace();
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
}
