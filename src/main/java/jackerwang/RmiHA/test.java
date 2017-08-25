package jackerwang.RmiHA;

import java.rmi.RemoteException;

public class test {

	
	public static void main(String[] args) {
		while (true) {
			ServerConsumer serverConsumer = new ServerConsumer();
			HelloWorldRemote helloWorldRemote = (HelloWorldRemote) serverConsumer.lookUp();
			try {
				helloWorldRemote.sayHello();
			} catch (RemoteException e) {

				e.printStackTrace();
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
	}
	
}
