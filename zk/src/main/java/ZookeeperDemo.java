import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lili on 2017/4/23.
 */
public class ZookeeperDemo {

    private static final String CONNECTION_STRING = "192.168.31.131:2181";
    private static final int SESSION_TIMEOUT = 5000;
    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zk = new ZooKeeper(CONNECTION_STRING, SESSION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    latch.countDown();
                }
            }
        });
        latch.await();
        System.out.println(zk);
        System.out.println("连接成功");
    }
}
