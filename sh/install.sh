# install three zk node in one machine
mkdir -p /opt/zookeeper
wget -P /opt/zookeeper https://downloads.apache.org/zookeeper/zookeeper-3.6.0/apache-zookeeper-3.6.0-bin.tar.gz
tar -zxvf /opt/zookeeper/apache-zookeeper-3.6.0-bin.tar.gz -C /opt/zookeeper/
mv /opt/zookeeper/apache-zookeeper-3.6.0-bin /opt/zookeeper/zookeeper1
cp -r /opt/zookeeper/zookeeper1/ /opt/zookeeper/zookeeper2
cp -r /opt/zookeeper/zookeeper1/ /opt/zookeeper/zookeeper3
mkdir -p /opt/zookeeper/zookeeper1/data && touch /opt/zookeeper/zookeeper1/data/myid && echo "1" >> /opt/zookeeper/zookeeper1/data/myid
mkdir -p /opt/zookeeper/zookeeper2/data && touch /opt/zookeeper/zookeeper2/data/myid && echo "2" >> /opt/zookeeper/zookeeper2/data/myid
mkdir -p /opt/zookeeper/zookeeper3/data && touch /opt/zookeeper/zookeeper3/data/myid && echo "3" >> /opt/zookeeper/zookeeper3/data/myid
touch /opt/zookeeper/zookeeper1/conf/zoo.cfg
touch /opt/zookeeper/zookeeper2/conf/zoo.cfg
touch /opt/zookeeper/zookeeper3/conf/zoo.cfg

## machine 2181:对外端口   2881:leader-Follower同步数据  3881:选举新leader
echo "syncLimit=5" >> /opt/zookeeper/zookeeper1/conf/zoo.cfg && \
echo "tickTime=2000" >> /opt/zookeeper/zookeeper1/conf/zoo.cfg && \
echo "initLimit=10" >> /opt/zookeeper/zookeeper1/conf/zoo.cfg && \
echo "dataDir=/opt/zookeeper/zookeeper1/data" >> /opt/zookeeper/zookeeper1/conf/zoo.cfg && \
echo "dataLogDir=/opt/zookeeper/zookeeper1/logs" >> /opt/zookeeper/zookeeper1/conf/zoo.cfg && \
echo "clientPort=2181" >> /opt/zookeeper/zookeeper1/conf/zoo.cfg && \
echo "server.1=0.0.0.0:2881:3881" >> /opt/zookeeper/zookeeper1/conf/zoo.cfg && \
echo "server.2=0.0.0.0:2882:3882" >> /opt/zookeeper/zookeeper1/conf/zoo.cfg && \
echo "server.3=0.0.0.0:2883:3883" >> /opt/zookeeper/zookeeper1/conf/zoo.cfg

## machine 2882:3882
echo "syncLimit=5" >> /opt/zookeeper/zookeeper2/conf/zoo.cfg && \
echo "tickTime=2000" >> /opt/zookeeper/zookeeper2/conf/zoo.cfg && \
echo "initLimit=10" >> /opt/zookeeper/zookeeper2/conf/zoo.cfg && \
echo "dataDir=/opt/zookeeper/zookeeper2/data" >> /opt/zookeeper/zookeeper2/conf/zoo.cfg && \
echo "dataLogDir=/opt/zookeeper/zookeeper2/logs" >> /opt/zookeeper/zookeeper2/conf/zoo.cfg && \
echo "clientPort=2182" >> /opt/zookeeper/zookeeper2/conf/zoo.cfg && \
echo "server.1=0.0.0.0:2881:3881" >> /opt/zookeeper/zookeeper2/conf/zoo.cfg && \
echo "server.2=0.0.0.0:2882:3882" >> /opt/zookeeper/zookeeper2/conf/zoo.cfg && \
echo "server.3=0.0.0.0:2883:3883" >> /opt/zookeeper/zookeeper2/conf/zoo.cfg

## machine 2883:3883
echo "syncLimit=5" >> /opt/zookeeper/zookeeper3/conf/zoo.cfg && \
echo "tickTime=2000" >> /opt/zookeeper/zookeeper3/conf/zoo.cfg && \
echo "initLimit=10" >> /opt/zookeeper/zookeeper3/conf/zoo.cfg && \
echo "dataDir=/opt/zookeeper/zookeeper3/data" >> /opt/zookeeper/zookeeper3/conf/zoo.cfg && \
echo "dataLogDir=/opt/zookeeper/zookeeper3/logs" >> /opt/zookeeper/zookeeper3/conf/zoo.cfg && \
echo "clientPort=2183" >> /opt/zookeeper/zookeeper3/conf/zoo.cfg && \
echo "server.1=0.0.0.0:2881:3881" >> /opt/zookeeper/zookeeper3/conf/zoo.cfg && \
echo "server.2=0.0.0.0:2882:3882" >> /opt/zookeeper/zookeeper3/conf/zoo.cfg && \
echo "server.3=0.0.0.0:2883:3883" >> /opt/zookeeper/zookeeper3/conf/zoo.cfg


echo "export JAVA_HOME=/opt/jdk/jdk1.8.0_181" >> /etc/profile && \
echo "export CLASSPATH=.:/opt/jdk/jdk1.8.0_181/lib/dt.jar:/opt/jdk/jdk1.8.0_181/lib/tools.jar" >> /etc/profile && \
echo "export ZOOKEEPER_HOME=/opt/zookeeper/zookeeper1" >> /etc/profile && \
echo "export PATH=.:$ZOOKEEPER_HOME/bin:/opt/jdk/jdk1.8.0_181/bin:$PATH" >> /etc/profile && \
source /etc/profile







