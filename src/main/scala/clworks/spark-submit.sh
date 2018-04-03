#!/bin/bash
#1.保证当前spark环境变量已经配置好
#2.jar包在当前路径下，否则指明绝对路径
#3.定期执行
externalJars=ojdbc6.jar
spark-submit --master yarn --deploy-mode cluster --driver-memory 5g  --num-executors 5  --executor-memory 8g  --executor-cores 2 --class com.chuanglan.Orc2Hive --jars ${externalJars}  chuanglan.jar
#指定jar包路径和传递参数
externalJar=/var/lib/hadoop-hdfs/ojdbc6.jar
spark-submit --master yarn --deploy-mode cluster --driver-memory 2g  --num-executors 5  --executor-memory 4g  --executor-cores 2 --class com.chuanglan.Orc2Hive --jars $externalJar  /var/lib/hadoop-hdfs/chuanglan.jar A1 zhiqun.a1
#每天执行命令，注意shell脚本必须可执行 '+x'。
#添加crontab命令时，无论命令是否有输出，最好都加上输出重定向到文件或者/dev/null中
# &>/dev/null 2>&1：标准输出重定向到空设备文件,也就是不输出任何信息到终端,标准错误输出重定向等同于标准输出
0 19 * * * sh /var/lib/hadoop-hdfs/toOracle.sh &>/dev/null 2>&1