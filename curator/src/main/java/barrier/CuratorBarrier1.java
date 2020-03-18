package barrier;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Random;

public class CuratorBarrier1 {

	/** zookeeper地址 */
	static final String CONNECT_ADDR = "192.168.31.121:2181,192.168.31.122:2181:2181,192.168.31.123:2181";
	/** session超时时间 */
	static final int SESSION_OUTTIME = 5000;//ms 
	
	public static void main(String[] args) throws Exception {
		
		
		//模拟分布式
		for(int i = 0; i < 5; i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
						CuratorFramework cf = CuratorFrameworkFactory.builder()
									.connectString(CONNECT_ADDR)
									.retryPolicy(retryPolicy)
									.build();
						cf.start();
						
						DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(cf, "/super", 5);
						Thread.sleep(1000 * (new Random()).nextInt(3)); 
						System.out.println(Thread.currentThread().getName() + "已经准备");
						barrier.enter();
						System.out.println("同时开始运行...");
						Thread.sleep(1000 * (new Random()).nextInt(3));
						System.out.println(Thread.currentThread().getName() + "运行完毕");
						barrier.leave();
						System.out.println("同时退出运行...");
						

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			},"t" + i).start();
		}

		
		
	}
}
