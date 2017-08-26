package jackerwang.RmiHA;

import java.rmi.RemoteException;

import junit.framework.TestCase;

public class publishS extends TestCase {
	public void pubServerA(){
		HelloWorldImpl helloWorldImpl = null;
		String host="localhost";
		String port="1900";
		try {
 			helloWorldImpl = new HelloWorldImpl();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		ServerProvider serverProvider = new ServerProvider();
		serverProvider.publishServer(host, Integer.valueOf(port), helloWorldImpl);
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void pubServerB(){
		HelloWorldImpl helloWorldImpl = null;
		String host="localhost";
		String port="1901";
		try {
 			helloWorldImpl = new HelloWorldImpl();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		ServerProvider serverProvider = new ServerProvider();
		serverProvider.publishServer(host, Integer.valueOf(port), helloWorldImpl);
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
