package watcher;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.List;

public class ZkClientWatcher1 {

	/** zookeeper地址 */
	static final String CONNECT_ADDR = "192.168.31.121:2181,192.168.31.122:2181:2181,192.168.31.123:2181";
	/** session超时时间 */
	static final int SESSION_OUTTIME = 5000;//ms 

	public static void main(String[] args) throws Exception {
		ZkClient zkc = new ZkClient(new ZkConnection(CONNECT_ADDR), 5000);
		
		//对父节点添加监听子节点变化。
		// 监控所有super字节点的变化。
		//只能监听子节点的删除或者新增或者自己节点的创建和删除
		zkc.subscribeChildChanges("/super", new IZkChildListener() {
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				//原生不会返回变化了的数据，还得自己读出来
				System.out.println("parentPath: " + parentPath);
				System.out.println("currentChilds: " + currentChilds);
			}
		});
		
		Thread.sleep(3000);
		
		zkc.createPersistent("/super");
		Thread.sleep(1000);
		
		zkc.createPersistent("/super" + "/" + "c1", "c1内容");
		Thread.sleep(1000);
		
		zkc.createPersistent("/super" + "/" + "c2", "c2内容");
		Thread.sleep(1000);

		//并不会监听字节的update操作
		zkc.writeData("/super"+"/"+"c1","C1新内容	");
		Thread.sleep(1000);
		
		zkc.delete("/super/c2");
		Thread.sleep(1000);	
		
		zkc.deleteRecursive("/super");
		Thread.sleep(Integer.MAX_VALUE);
		
		
	}
}
