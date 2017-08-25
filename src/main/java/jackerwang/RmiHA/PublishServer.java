package jackerwang.RmiHA;

import java.rmi.RemoteException;

public class PublishServer {

	public static void main(String[] args) {
		HelloWorldImpl helloWorldImpl = null;
		String host=args[0];
		String port=args[1];
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
