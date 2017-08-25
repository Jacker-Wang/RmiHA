package jackerwang.RmiHA;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HelloWorldImpl extends UnicastRemoteObject implements HelloWorldRemote{

	protected HelloWorldImpl() throws RemoteException {
		super();
	}

	public void sayHello() {
		System.out.println("helloWorld");
	}

}
