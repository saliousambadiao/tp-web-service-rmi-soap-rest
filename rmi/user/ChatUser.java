/**
 * Author: Saliou Samba DIAO
 * Email : saliousambadiao@esp.sn
 * Date  : 2025-05-11
 */
package user;
import java.rmi.*;

public interface ChatUser extends Remote {
    public void displayMessage(String message) throws RemoteException;
}
