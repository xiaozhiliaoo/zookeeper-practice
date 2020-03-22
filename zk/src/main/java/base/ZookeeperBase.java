package base;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.lili.common.PropsUtil;

import java.util.concurrent.CountDownLatch;


public class ZookeeperBase {

	/** zookeeper地址 */
	static final String CONNECT_ADDR = PropsUtil.getString("zk-config");
	/** session超时时间 */
	static final int SESSION_OUTTIME = 2000;//ms 
	/** 信号量，阻塞程序执行，用于等待zookeeper连接成功，发送成功信号 */
	static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	public static void main(String[] args) throws Exception{

		// 代码是异步的  new zk实例后，代码会一直往下走   Watcher是回调  链接Zk集群时异步的  没连接上肯定不能往下走
		ZooKeeper zk = new ZooKeeper(CONNECT_ADDR, SESSION_OUTTIME, new Watcher(){
			@Override
			public void process(WatchedEvent event) {
				//获取事件的状态
				KeeperState keeperState = event.getState();
				// 获取事件的类型
				EventType eventType = event.getType();
				//如果是建立连接
				if(KeeperState.SyncConnected == keeperState){
					//刚开始链接什么都没做
					if(EventType.None == eventType){
						//如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
						connectedSemaphore.countDown();
						System.out.println("zk 建立连接");
					}
				}
			}
		});

		//进行阻塞  zk真正链接成功后在往下走  最原始的写法
		connectedSemaphore.await();
		
		System.out.println("......执行啦.......");


		//创建父节点
//		zk.create("/testRoot", "testRoot".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		//创建子节点
//		String s = zk.create("/testRoot/children", "children data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		//临时节点本地会话有效
//		String s = zk.create("/testRoot/children", "children data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
//		System.out.println(s);


		//-1全部清空   相当于版本号
		/*zk.delete("/testRoot", -1, new AsyncCallback.VoidCallback() {
			@Override
			public void processResult(int rc, String path, Object ctx) {
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(rc);
				System.out.println(path);
				System.out.println(ctx);
			}
		},"a");//a属于参数回调   在callback里面显示并输出

		TimeUnit.SECONDS.sleep(5);    //没有休眠的话，直接会zk.close()  */

		//获取节点信息
//		byte[] data = zk.getData("/testRoot", false, null);
//		System.out.println(new String(data));
//		System.out.println(zk.getChildren("/testRoot", false));
		
		//修改节点的值
//		zk.setData("/testRoot", "modify data root".getBytes(), -1);
//		byte[] data = zk.getData("/testRoot", false, null);
//		System.out.println(new String(data));		
		
		//判断节点是否存在
		System.out.println(zk.exists("/testRoot/children", false));
//		38654705690,38654705690,1496829341304,1496829341304,0,0,0,0,13,0,38654705690
		//删除节点
//		zk.delete("/testRoot/children", -1);
//		System.out.println(zk.exists("/testRoot/children", false));

		// 只可以取一层  这么设计有原因的
		/*List<String> children = zk.getChildren("/testRoot2", false);
		for(String path:children){
			System.out.println("节点路径： "+path);
			String realPath = "/testRoot2/"+path;
			System.out.println("节点数据是："+new String(zk.getData(realPath,false,null)));
		}*/


		zk.close();
	}
}
