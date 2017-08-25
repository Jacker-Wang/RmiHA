package jackerwang.RmiHA;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HelloWorldRemote extends Remote{
	public void sayHello() throws RemoteException;

}
