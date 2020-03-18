package cluster;


public class Client2 {

	public static void main(String[] args) throws Exception{
		
		CuratorWatcher watcher = new CuratorWatcher();
		System.out.println("Client2机器启动.....");
		Thread.sleep(100000000);
	}
}
