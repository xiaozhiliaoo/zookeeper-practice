package watcher;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.lili.common.PropsUtil;

public class CuratorWatcher2 {
	
	/** zookeeper地址 */
	static final String CONNECT_ADDR = PropsUtil.getString("zk-config");
	/** session超时时间 */
	static final int SESSION_OUTTIME = 5000;//ms 
	
	public static void main(String[] args) throws Exception {
		
		//1 重试策略：初试时间为1s 重试10次
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
		//2 通过工厂创建连接
		CuratorFramework cf = CuratorFrameworkFactory.builder()
					.connectString(CONNECT_ADDR)
					.sessionTimeoutMs(SESSION_OUTTIME)
					.retryPolicy(retryPolicy)
					.build();
		
		//3 建立连接
		cf.start();
		
		//4 建立一个PathChildrenCache缓存,第三个参数为是否接受节点数据内容 如果为false则不接受
		PathChildrenCache cache = new PathChildrenCache(cf, "/super", true);
//		PathChildrenCache cache = new PathChildrenCache(cf, "/super", false); //不显示改变的值
		//5 在初始化的时候就进行缓存监听
		cache.start(StartMode.POST_INITIALIZED_EVENT);  //一般使用这一个
//		cache.start(StartMode.BUILD_INITIAL_CACHE);  //不监听子节点变更，监听当前节点变更
//		cache.start(StartMode.NORMAL);
		cache.getListenable().addListener(new PathChildrenCacheListener() {
			/**
			 * <B>方法名称：</B>监听子节点变更<BR>
			 * <B>概要说明：</B>新建、修改、删除<BR>
			 * @see org.apache.curator.framework.recipes.cache.PathChildrenCacheListener#childEvent(org.apache.curator.framework.CuratorFramework, org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent)
			 */

			public void childEvent(CuratorFramework cf, PathChildrenCacheEvent event) throws Exception {
				switch (event.getType()) {
				case CHILD_ADDED:
					System.out.println("CHILD_ADDED_PATH :" + event.getData().getPath());
					System.out.println("CHILD_ADDED_DATA :"+new String(event.getData().getData(),"utf-8"));
					break;
				case CHILD_UPDATED:
					System.out.println("CHILD_UPDATED_PATH :" + event.getData().getPath());
					System.out.println("CHILD_UPDATED_DATA :"+new String(event.getData().getData(),"utf-8"));
					break;
				case CHILD_REMOVED:
					System.out.println("CHILD_REMOVED_PATH :" + event.getData().getPath());
					System.out.println("CHILD_UPDATED_DATA :"+new String(event.getData().getData(),"utf-8"));
					break;
				default:
					break;
				}
			}
		});

		//创建本身节点不发生变化
		cf.create().forPath("/super", "init".getBytes());
		
		//添加子节点
		Thread.sleep(1000);
		cf.create().forPath("/super/c1", "c1内容".getBytes());
		Thread.sleep(1000);
		cf.create().forPath("/super/c2", "c2内容".getBytes());

//		cf.create().forPath("/super/c2/c3","c3hahaha".getBytes());
//Exception in thread "main" org.apache.zookeeper.KeeperException$NotEmptyException: KeeperErrorCode = Directory not empty for /super/c2
		//只能监听一级节点
		
		//修改子节点
		Thread.sleep(1000);
		cf.setData().forPath("/super/c1", "c1更新内容".getBytes());
		
		//删除子节点
		Thread.sleep(1000);
		cf.delete().forPath("/super/c2");		
		
		//删除本身节点
		Thread.sleep(1000);
		cf.delete().deletingChildrenIfNeeded().forPath("/super");
		
		Thread.sleep(Integer.MAX_VALUE);
		

	}
}
