#!/usr/bin/env bash

# 使用变量${变量名}
a="abcdef"
readonly a  # 只读变量
echo ${a}

b="abc"
unset b  # 删除变量
echo ${b}

# 循环
for i in a b c;
do
echo "${i}"
done

# 拼接字符串
echo "hello${a},${b}"

# 获取字符串长度
echo ${#a}

#提取子字符串,索引从0开始
echo ${a:1:3}

# 查找子字符串
echo `expr index ${a} b` # expr是表达式计算工具

# 数组，空格分割
arr=(a b c d)
echo ${arr[1]} #读取
echo ${arr[*]} #读取所有元素
echo ${#arr[*]} #获取数组长度

# 参数传递 $n  n表示第n个参数
echo "$0"   # 为执行的文件名

echo `expr 3 + 2`
echo `expr  3 \* 2 ` #注意空格,乘号(*)前边必须加反斜杠(\)才能实现乘法运算

if [ 3 -eq 2 ]
then
echo "^_^"
else
echo "----"
fi

# 布尔运算符  非"!" ，或"-o" ，与"-a"
if [ 3 -gt 2 -a 4 -gt 5 ]
then
echo "!!!!!!!"
else
echo "@@@@@"
fi

# 逻辑运算符 &&和||
if [[ 3 -gt 2 || 4 -gt 5 ]]
then
echo "!!!!!!!"
else
echo "@@@@@"
fi

#字符串是否为0  "-z，-n"
if [ -z "a" ]
then
echo "equal"
else
echo "unequal"
fi

# -e:开启转义命令
echo  -e "a \nb"
echo -e "\"a\""

# 加上$可以允许命令或者运算
res=$[ 1 + 2 ]
echo ${res}

# if格式
if condition
then
    command1
    command2
    ...
    commandN
fi

if ( $(ps -ef | grep -c "ssh") -gt 1 )
then
echo "true"
else
echo "false"
fi

# for格式
for var in item1 item2 ... itemN
do
    command1
    command2
    ...
    commandN
done

for i in 1 2 3
do
echo $[ ${i} * ${i} ]
done

# while格式
while condition
do
    command
done

int=1
while [ ${int} -le 2 ]
do
echo $int
let "int++"  #let用来数值运算,变量计算中不需要加上 $ 来表示变量
done

#case语法
case 值 in
模式1)
    command1
    command2
    ...
    commandN
    ;;
esac



#case 选择
echo '输入 1 到 4 之间的数字:'
echo '你输入的数字为:'
read aNum  #从标准输入中读取一行，并把输入行的每个字段的值指定给 shell 变量
case $aNum in
1) echo "你选择了 1"
;;
2) echo '你选择了 2'
;;
3) echo '你选择了 3'
;;
4) echo '你选择了 4'
continue
;;
*) echo '你没有输入 1 到 4 之间的数字'
;;
esac

func(){
echo "input"
read nu
return ${nu}
}

# /dev/null 是一个特殊的文件，写入到它的内容都会被丢弃；如果尝试从该文件读取内容，那么什么也读不到
echo `expr 2 * 9 `
#等价于
echo $(expr 2 \* 9)

echo `seq 1 9`  #输出

#[]表示直接求值
echo $[1+2]  《==》 echo `expr 1 + 2`

i=1
sum=0
while [ $i -le 100 ]
do
sum=$[$sum+$i]
let "i++"
done
echo $sum


