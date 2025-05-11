package user;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import window.ChatWindow;

@SuppressWarnings("")
public class ChatUserImpl extends UnicastRemoteObject implements ChatUser {

    private ChatWindow chatWindow;

    public ChatUserImpl() throws RemoteException
	{
		super();
	}

    public void setIg(ChatWindow chatWindow)
	{
		this.chatWindow = chatWindow;
	}

    @Override
	public void displayMessage(String message) throws RemoteException
	{
		chatWindow.displayMessage(message);
	}
}
