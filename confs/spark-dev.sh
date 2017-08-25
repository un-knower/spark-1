#!/bin/bash
#确保先安装Hadoop

#1、安装spark
#http://spark.apache.org/downloads.html
#wget http://d3kbcqa49mib13.cloudfront.net/spark-2.1.1-bin-hadoop2.7.tgz

cd /home/ztgame/soft/
tar zxvf spark-2.1.1-bin-hadoop2.7.tgz
ln -s /home/ztgame/soft/spark-2.1.1-bin-hadoop2.7 /home/ztgame/soft/spark

#2、配置
#1) 设置Spark环境变量
#vim ~/.bash_profile 或 /etc/profile
#export SPARK_HOME=/home/ztgame/soft/spark
#export PATH=$SPARK_HOME/bin:$PATH
sed -i '$a#set for SPARK_HOME' ~/.bash_profile
echo "export SPARK_HOME=/home/ztgame/soft/spark" >> ~/.bash_profile
sed -i '$aexport PATH=$SPARK_HOME/bin:$PATH' ~/.bash_profile
source ~/.bash_profile

echo $SPARK_HOME

#2)修改spark-env.sh
cd $SPARK_HOME
cp ./conf/spark-env.sh.template ./conf/spark-env.sh
#vim ./conf/spark-env.sh
#加入
#export SPARK_DIST_CLASSPATH=$(/home/ztgame/soft/hadoop/bin/hadoop classpath)

#export SPARK_CLASSPATH=$SPARK_CLASSPATH:/home/ztgame/soft/mysql-connector-java-5.1.38.jar

#3)运行测试 -------------------------------
cd $SPARK_HOME
./bin/run-example SparkPi
#./sbin/spark-daemon.sh start

#4) 集群启动 -------------------------------
cd $SPARK_HOME
#Master
#vim ./conf/spark-env.sh
echo "export SPARK_MASTER_HOST=spark-slave-01    " >> ./conf/spark-env.sh
echo "export SPARK_MASTER_PORT=17077             " >> ./conf/spark-env.sh
echo "export SPARK_MASTER_WEBUI_PORT=18080       " >> ./conf/spark-env.sh
echo "export SPARK_WORKER_PORT=18090             " >> ./conf/spark-env.sh
echo "export SPARK_WORKER_WEBUI_PORT=18081       " >> ./conf/spark-env.sh
echo "export SPARK_WORKER_MEMORY=5g              " >> ./conf/spark-env.sh
echo "export SPARK_DAEMON_MEMORY=2g              " >> ./conf/spark-env.sh
#Slaves
rm -f ./conf/slaves
cp ./conf/slaves.template ./conf/slaves
sed -i '$aspark-slave-01' ./conf/slaves
#echo "spark-slave-01" >> ./conf/slaves
echo "spark-slave-02" >> ./conf/slaves
echo "spark-slave-03" >> ./conf/slaves

#配置域名
echo "45.115.147.209   spark-slave-01" >> /etc/hosts
echo "45.115.147.209   spark-slave-02" >> /etc/hosts
echo "45.115.147.209   spark-slave-03" >> /etc/hosts

#设置自动登录
#ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
#cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
#chmod 600 ~/.ssh/authorized_keys

#全部启动
./sbin/start-all.sh

#独立启动
./sbin/start-master.sh
./sbin/start-slave.sh 3 spark://spark-slave-01:17077 --webui-port 8090

#5)Spark Shell 进行交互分析
./bin/spark-shell



#5)集群测试
cd $SPARK_HOME
http://45.115.147.209:18080

#6)依赖包下载安装
#http://mvnrepository.com/artifact/org.apache.spark
#https://spark-packages.org/
#$SPARK_HOME/bin/spark-shell --packages dibbhatt:kafka-spark-consumer:1.0.10

#$SPARK_HOME/bin/spark-shell --packages org.apache.kafka:kafka-clients:0.10.2.1
#$SPARK_HOME/bin/spark-shell --packages org.apache.spark:spark-streaming-kafka_2.11:1.6.3
#$SPARK_HOME/bin/spark-shell --packages org.apache.spark:spark-streaming-kafka-0-10_2.11:2.1.1
#下载依赖包spark-streaming-kafka_2.11-1.6.3.jar，放到$SPARK_HOME/jars/目录
#kafka.serializer
#kafka_2.11:0.10.2.1

#http://spark.apache.org/docs/latest/streaming-kafka-0-10-integration.html
#http://mvnrepository.com/artifact/org.apache.spark/spark-streaming-kafka-0-10_2.11/2.1.1
#spark-streaming-kafka-0-10_2.11-2.1.1.jar
cp $KAFKA_HOME/libs/kafka-clients-0.10.2.1.jar $SPARK_HOME/jars/
cp $KAFKA_HOME/libs/kafka_2.11-0.10.2.1.jar $SPARK_HOME/jars/
cp $KAFKA_HOME/libs/zkclient-0.10.jar $SPARK_HOME/jars/
cp $KAFKA_HOME/libs/metrics-core-2.2.0.jar $SPARK_HOME/jars/