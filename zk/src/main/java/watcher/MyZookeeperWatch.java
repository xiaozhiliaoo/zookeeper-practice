package watcher;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.lili.common.PropsUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lili on 2017/6/9.
 */
public class MyZookeeperWatch implements Watcher {

    AtomicInteger seq = new AtomicInteger();
    private static final int SESSION_TIMEOUT = 10000;
    private static final String CONNECTION_ADDR = PropsUtil.getString("zk-config");
    private static final String PARENT_PATH = "/testWatch";
    private static final String CHILDREN_PATH = "/testWatch/children";
    private static final String LOG_PREFIX_OF_MAIN = "【Main】";
    private ZooKeeper zk = null;
    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public void createConnection(String connectAddr, int sessionTimeout){
        this.releaseConnection();

        try {
            zk = new ZooKeeper(connectAddr, sessionTimeout, this);
            System.out.println(LOG_PREFIX_OF_MAIN + " 开始连接ZK服务器");
            connectedSemaphore.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void releaseConnection() {
        if(this.zk != null){
            try {
                this.zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean createPath(String path, String data, boolean needWatch){
        try {
            this.zk.exists(path,needWatch);
            System.out.println(LOG_PREFIX_OF_MAIN+" 节点创建成功，Path："+ this.zk.create(path,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)+", connect: " + data);
        } catch (KeeperException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String readData(String path, boolean needWatch){
        try {
            return new String(this.zk.getData(path,needWatch,null));
        } catch (KeeperException e) {
            e.printStackTrace();
            return "";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean writeData(String path, String data){
        try {
            System.out.println(LOG_PREFIX_OF_MAIN + "更新数据成功，path：" + path + ", stat: " +
                    this.zk.setData(path, data.getBytes(), -1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteNode(String path) {
        try {
            this.zk.delete(path, -1);
            System.out.println(LOG_PREFIX_OF_MAIN + "删除节点成功，path：" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Stat exists(String path, boolean needWatch) {
        try {
            return this.zk.exists(path, needWatch);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> getChildren(String path, boolean needWatch){
        try {
            return this.zk.getChildren(path,needWatch);
        } catch (KeeperException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteAllTestPath(){
        if(this.exists(CHILDREN_PATH,false) != null){
            this.deleteNode(CHILDREN_PATH);
        }
        if(this.exists(PARENT_PATH,false) != null){
            this.deleteNode(PARENT_PATH);
        }
    }




    @Override
    public void process(WatchedEvent event) {
        System.out.println("进入Process........event="+event);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(event == null){
            return;
        }
        Event.KeeperState keeperState = event.getState();
        Event.EventType eventType = event.getType();
        String path = event.getPath();
        String logPrefix = "【Watcher-" + this.seq.incrementAndGet() + "】";
        System.out.println(logPrefix + "收到Watcher通知");
        System.out.println(logPrefix + "连接状态:\t" + keeperState.toString());
        System.out.println(logPrefix + "事件类型:\t" + eventType.toString());
        if(keeperState.SyncConnected == keeperState){
            if(Event.EventType.None == eventType){
                System.out.println(logPrefix+" 成功连接上服务器");
                connectedSemaphore.countDown();
            }else if(Event.EventType.NodeCreated == eventType){
                System.out.println(logPrefix+" 节点创建");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.exists(path,true);
            }else if(Event.EventType.NodeDataChanged == eventType){
                System.out.println(logPrefix + "节点数据更新");
                System.out.println("我看看走不走这里........");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(logPrefix + "数据内容: " + this.readData(PARENT_PATH, true));
            }else if(Event.EventType.NodeChildrenChanged == eventType){
                System.out.println("子节点变更");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(logPrefix+" 子节点列表："+this.getChildren(PARENT_PATH,true));
            }else{

            }
        }else if(Event.KeeperState.Disconnected == keeperState){
            System.out.println(logPrefix+"与ZK服务器断连");
        }else if(Event.KeeperState.AuthFailed == keeperState){
            System.out.println(logPrefix+"权限检查失败");
        }else if(Event.KeeperState.Expired == keeperState){
            System.out.println(logPrefix+"会话失效");
        }else{

        }
        System.out.println("----------------------------------------------------");
    }


    public static void main(String[] args) throws InterruptedException {
        ZooKeeperWatcher zooKeeperWatcher = new ZooKeeperWatcher();
        zooKeeperWatcher.createConnection(CONNECTION_ADDR,SESSION_TIMEOUT);
        Thread.sleep(1000);

    }
}
