package jackerwang.RmiHA;

import java.io.IOException;
import java.lang.invoke.MethodHandles.Lookup;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event;
import org.apache.zookeeper.Watcher.Event.EventType;

public class ServerConsumer {
	static volatile List<String> urList = null;
	ZooKeeper zk = null;

	public ServerConsumer() {
		zk=connectZoo(Constant.ZooHost, Constant.ZooPort);
		if(zk!=null) {
			System.out.println("ServerConsumer连接上zookeeper，host:" + Constant.ZooHost + "---port:" + Constant.ZooPort);
     	} 
		else {
			System.out.println("ServerConsumer连接zookeeper失败");
		}
		watchNode(zk);
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
						System.out.println("ServerConsumer连接上Zoo");
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

	// 得到zookeeper的事件改变
	private void watchNode(final ZooKeeper zk) {
		List<String> dataList = new ArrayList<String>();
		try {
			dataList = zk.getChildren(Constant.REGISTRYZ_PATH, new Watcher() {
				public void process(WatchedEvent event) {
					// 如果观察到节点变化，则得到最新状态的节点信息,即继续观察节点
					if (event.getType() == EventType.NodeChildrenChanged) {
						watchNode(zk);
					}
				}
			});
			// 得到每个节点中的数据
			List<String> tmpList = new ArrayList<String>();
			for (String node : dataList) {
				String url = String.valueOf(node.getBytes());
				tmpList.add(url);
			}
			urList = tmpList;

		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//需找远程server
	private <T> T lookUpServer(String url) {
		T server=null;
		try {
			server=(T)Naming.lookup(url);
		} catch (Exception e) {
			//如果连接失败，则选取第一个url进行重新连接
			if(e instanceof ConnectException) {
				url=urList.get(0);
				lookUpServer(url);
			}
			e.printStackTrace();
		}
		return server;
	}
	
	//通过urlList寻找远程url
	public <T extends Remote> T lookUp(){
		T server=null;
		int size=urList.size();
		String url=null;
		if(size>0) {
			if(size>1) {
				url=urList.get(ThreadLocalRandom.current().nextInt(size));
			}
			else {
				url=urList.get(0);
			}
		}
		server=lookUpServer(url);
		return server;
	}
}
