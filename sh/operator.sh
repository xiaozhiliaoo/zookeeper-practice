lsof -i :2183
rm -rf /opt/zookeeper/zookeeper1/conf/zoo.cfg && \
rm -rf /opt/zookeeper/zookeeper2/conf/zoo.cfg && \
rm -rf /opt/zookeeper/zookeeper3/conf/zoo.cfg && \
rm -rf /opt/zookeeper/zookeeper1/data/version-2/ && \
rm -rf /opt/zookeeper/zookeeper2/data/version-2/ && \
rm -rf /opt/zookeeper/zookeeper3/data/version-2/ && \
rm -rf /opt/zookeeper/zookeeper1/data/zookeeper_server.pid && \
rm -rf /opt/zookeeper/zookeeper2/data/zookeeper_server.pid && \
rm -rf /opt/zookeeper/zookeeper3/data/zookeeper_server.pid
ps -ef | grep 'zookeeper' | grep -v grep | awk '{print $2}' | xargs kill -9


zkCli.sh -server localhost:2181