package cluster;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * Created by lili on 2017/6/10.
 */
public class MyCuratorWatcher {
    static final String PARENT_PATH = "/super";
    public static final String CONNECT_ADDR = "";
    public static final int SESSION_TIMEOUT = 30000;

    public MyCuratorWatcher() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,10);
        CuratorFramework cf = CuratorFrameworkFactory.builder().connectString(CONNECT_ADDR)
                .sessionTimeoutMs(SESSION_TIMEOUT).retryPolicy(retryPolicy).build();

        cf.start();

        if(cf.checkExists().forPath(PARENT_PATH) == null){
            cf.create().withMode(CreateMode.PERSISTENT).forPath(PARENT_PATH,"super init".getBytes());
            PathChildrenCache pathChildrenCache = new PathChildrenCache(cf,PARENT_PATH,true);
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    switch (pathChildrenCacheEvent.getType()){
                        case CHILD_ADDED:
                        case CHILD_UPDATED:
                        case CHILD_REMOVED:
                            default:
                    }
                }
            });
        }
    }
}
