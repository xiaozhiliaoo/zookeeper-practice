package cluster;


public class Client1 {

	public static void main(String[] args) throws Exception{
		CuratorWatcher watcher = new CuratorWatcher();
		System.out.println("Client1机器启动.....");
		Thread.sleep(100000000);
	}
}
