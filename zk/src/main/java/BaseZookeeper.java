import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.lili.common.PropsUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lili on 2017/4/23.
 */
public class BaseZookeeper implements Watcher {

    private ZooKeeper zooKeeper;
    public static final int SESSION_OUT = 2000;
    private CountDownLatch latch = new CountDownLatch(1);

    public void connectZookeeper(String host) throws IOException, InterruptedException {
        //Ctrl+p查看方法参数
        zooKeeper = new ZooKeeper(host, SESSION_OUT, this);
        latch.await();
        System.out.println("zookeeper connect ok");
    }
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            System.out.println("watcher receive event");
            latch.countDown();
        }
    }
    public String createNode(String path, byte[] data) throws KeeperException, InterruptedException {
        return zooKeeper.create(path,data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public List<String> getChildren(String path) throws KeeperException,
            InterruptedException {
        return zooKeeper.getChildren(path, false);
    }

    public Stat setData(String path, byte [] data, int version) throws KeeperException, InterruptedException {
        return zooKeeper.setData(path, data, version);
    }

    public byte[] getData(String path) throws KeeperException, InterruptedException {
        return zooKeeper.getData(path,false,null);
    }

    public void deletNode(String path, int version)
            throws InterruptedException, KeeperException {
        this.zooKeeper.delete(path, version);
    }

    /**
     * 关闭zookeeper连接
     *
     * @throws InterruptedException
     */
    public void closeConnect() throws InterruptedException {
        if (null != zooKeeper) {
            zooKeeper.close();
        }
    }

    public static void main(String[] args) throws Exception{
        BaseZookeeper baseZookeeper = new BaseZookeeper();

        String host = PropsUtil.getString("zk-config");

        // 连接zookeeper
        baseZookeeper.connectZookeeper(host);
        System.out.println("--------connect zookeeper ok-----------");

        // 创建节点
        byte [] data = {1, 2, 3, 4, 5};
        String result = baseZookeeper.createNode("/test", data);
        System.out.println(result);
        System.out.println("--------create node ok-----------");

        // 获取某路径下所有节点
        List<String> children = baseZookeeper.getChildren("/");
        for (String child : children)
        {
            System.out.println(child);
        }
        System.out.println("--------get children ok-----------");

        // 获取节点数据
        byte [] nodeData = baseZookeeper.getData("/test");
        System.out.println(Arrays.toString(nodeData));
        System.out.println("--------get node data ok-----------");

        // 更新节点数据
        data = "test data".getBytes();
        baseZookeeper.setData("/test", data, 0);
        System.out.println("--------set node data ok-----------");

        nodeData = baseZookeeper.getData("/test");
        System.out.println(Arrays.toString(nodeData));
        System.out.println("--------get node new data ok-----------");

        baseZookeeper.closeConnect();
        System.out.println("--------close zookeeper ok-----------");
    }
}

