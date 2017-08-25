package jackerwang.RmiHA;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.apache.zookeeper.ZooKeeper;

public class ServerProvider {
	static StringBuilder url = new StringBuilder("rmi://");
	ZooKeeper zooKeeper = null;

	// 创建ServerProvider实例的时候连接zookeeper
	public ServerProvider() {
		zooKeeper = connectZoo(Constant.ZooHost, Constant.ZooPort);
		if (zooKeeper != null) {
			System.out.println("ServerProvider连接上zookeeper，host:" + Constant.ZooHost + "---port:" + Constant.ZooPort);
		} else {
			System.out.println("ServerProvider连接zookeeper失败");
		}
	}

	// 将服务server发布到host:port
	public void publishServer(String host, Integer port, Remote server) {
		String serviceName = server.getClass().getName();
		url = url.append(host + ":" + port + "/" + serviceName);
		try {
			LocateRegistry.createRegistry(port);
			Naming.rebind(url.toString(), server);
			// 将服务的url注册到zookeeper，创建为zookeeper的临时节点。
			if (zooKeeper != null) {
				createNode(zooKeeper, url.toString());
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	// 连接zookeeper
	private ZooKeeper connectZoo(String host, Integer port) {
		String connectionUrl = host + ":" + String.valueOf(port);
		ZooKeeper zooKeeper = null;
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		try {
			zooKeeper = new ZooKeeper(connectionUrl, Constant.CONNECTION_TIMEOUT, new Watcher() {
				public void process(WatchedEvent event) {
					// 如果连接上zookeeper,countDownLatch值减一
					if (event.getState() == Event.KeeperState.SyncConnected) {
						countDownLatch.countDown();
						System.out.println("ServiceProvider连接上Zoo");
					}
				}
			});
			// 没有连接上zookeeper服务之前，线程阻塞。
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return zooKeeper;
	}

	// 在zookeeper中创建临时节点
	private String createNode(ZooKeeper zk, String serverUrl) {
		byte[] data = serverUrl.getBytes();
		String path = null;
		try {
			path = zk.create(Constant.REGISTRYZ_PATH, data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;
	}

}
