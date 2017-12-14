import java.util
import java.util.UUID

import edu.stanford.nlp.ling.CoreAnnotations.SRLInstancesAnnotation

import scala.collection.immutable.Range
import scala.concurrent.Future
import scala.runtime.Nothing$
import scala.util.{Failure, Success, Try}

val a="hello"=="hello"
val b="a"*10
val a1="hello"
val a2=new String("hello")
a1.equals(a2)
println(s"${b*2}")
val reg=".*([\\d.+]).*".r
val in="dfsf2.343df"
val reg(num)=in
num
val char:Char=116

//表达式的返回值赋给变量
val x={
  5*20
}

val hour="1"
val cla=hour match {
  case "1"=>println("1")
  case "1"=>println(2)  //多个相同的匹配表达式只会匹配一个，其他的无返回值Unit
  case gfhr=>println("2")
}

def estimate(hour:Int)={
 /* if(hour>=0 && hour<=6){
    hour.toString.replace(hour.toString,"1")
  }else if(hour>=7 && hour<=12){
    hour.toString.replace(hour.toString,"2")
  }else if(hour>=13 && hour<=18){
    hour.toString.replace(hour.toString,"3")
  }else if(hour>=19 && hour<=23){
    hour.toString.replace(hour.toString,"4")
  }*/

/*  hour match {
      //管道符号匹配
    case 0|1|2|3=> hour.toString.replace(hour.toString,"1")
      //值匹配局部变量匹配任何输入值,并输出输入值
   // case sdasf=>println(sdasf)
      //不能在表达式中使用通配符"_"
    case _=>println(hour)
  }*/

  hour match {
      //使用条件表达式
    case s if (s>=1 & s<=3)=>println(s)
    case s=>println(s)
  }

}

estimate(4)

//匹配类型
val ca:Int=424234
val cas:Any=ca
cas match {
  case x:String=>println(x)
  case x:Double=>println(x)
  case x:Int=>println(x)
  case x:Any=>println(x)
}

//返回包含表达式返回值的集合IndexedSeq，也可进入下一个循环进行迭代
//range相当于until
//当表达式中只有一个命令时，可以省略大括号
val yie=for(x<-Range(1,6)) yield {
  x
}
for (i<-yie) println(i)

//迭代条件使用if语句,可以使用多个if
for(i<-Range(1,5) if i%2==0) yield i
for(
  i<-Range(1,5)
  if i%2==0  //多次过滤，使用多个if
  if i>3
)yield i

//for中多个迭代器,注意使用大括号
//迭代总数是所有迭代器的乘积
for{i<-1 to 3
  j<-1 to 2} yield (i,j)

for(i<-1 to 100){
  print(i+",")
  if(i%5==0) println()
}

//函数是可重用的命名表达式
//定义无参数函数,返回固定的值,可选，指定返回类型
def he():String="hello"
//def he="hello"
he
//函数的最后一行作为函数的返回值
//return关键字显示指定函数的返回值，并退出函数
//无返回类型Unit
//可以向函数参数传递表达式

def he(x:Double) ={
 x+1
}
he{
  val a=2
  val b=3
  a*b  //表达式返回值作为函数参数传入
}

//递归函数,在函数中调用自身
def power(x:Int,y:Int):Long={
  if(y>=1) x*power(x,y-1)
  else 1
}

/**
  * 正数次幂
  * 2*power(2,2)=>2*2*power(2,1)=>2*2*2*power(2,0)=>2*2*2*1
  */
power(2,3)

//尾递归优化,防止栈溢出，最后一个语句必须是递归调用的函数
//在函数定义前增加

//嵌套函数,只能在外部函数内部使用
def max(a:Int,b:Int,c:Int)={
  def max(x:Int,y:Int)=if (x>y) x else y
  max(a,max(b,c))
}
max(1,31,353)

//为参数设置默认值,一般默认参数位置在最后面
def moren(a:String="a",b:String)=a+b
moren(b="b")

//可变参数vararg,本质是Seq，可以作为for循环的迭代器
def sum(a:Int*)={
  var total=0
  for(i<-a) total+=i
  total
}

sum(1,2,3)

//参数组，用小括号分割
def max(x:Int)(y:Int)={
  if(x>y)x else y
}
max(2)(4)

//类型参数,适用于调用者想要使用的任何类型，定义函数的返回类型
def fan[A](s:A):A={
  s
}
//调用函数可以省略类型参数，因为可以根据传入的参数类型推断函数返回类型
fan(2)

//函数不仅可以声明和调用，还可以作为数据类型用在任何地方

//函数的类型：基于输入参数类型和返回值的类型
def dou(x:Int)=x*2
//将函数作为数据赋给一个值，但可以调用该值
//函数值必须显示指明类型或者使用通配符
//val mydou:(Int)=>Int=dou
//通配符的意思表示将来的一个函数调用的返回值存储在值中，相当于这个函数的快捷方式
val mydou=dou _
//函数值也可以赋给其他值
val mydou2=mydou
mydou2(2)

//高阶函数：一个函数接受其他函数作为参数，或者使用函数作为返回值

//函数作为参数，直接使用函数名
def sn(s:String,f:String=>String)={
  if(s!=null)f(s) else s
}
def re(s:String)=s.reverse
sn(null,re)
sn("adsa",re)

//匿名函数,定义有类型的输入参数和函数体，可以存储在函数值或变量中，或者高阶函数的一部分
val nim=(x:Int)=>x*2
nim(2)

sn("wqeq",(s:String)=>s.reverse)

//占位符出现情况：函数类型已经显示指定或者输入参数最多使用一次
val zhan:Int=>Int=_*2
sn("asd",_.reverse)

//函数柯里化，函数具有多个参数表，只应用一个参数表的参数，另一个参数表不应用
def keli(x:Int)(y:Int)=y%x==0
val kelihua=keli(2) _  //函数复制特殊情况
val k=kelihua(10)

//在函数中使用大的表达式块
def kuai(s:String)(f:String=>String)= {
  if (s != null) f(s) else s
}
val uuid=UUID.randomUUID().toString
val time=kuai(uuid){s=>
  val now=System.currentTimeMillis()
  val timed=s.take(24)+now
  timed
}

val list=List(1,2,3)
list.reduce((a,b)=>a+b)

//可以将元素加入Set集合，自动去重
//所有列表都有一个Nil实例作为终结点，Nil是List[Nothing]的一个单例实例
 val kl=List()
kl==Nil  //true
//也可以使用::创建list,注意末尾必须加上Nil
val cons=1::2::3::Nil

List(1,2)::List(3,4)  //在列表前追加一个列表
List(1,2):::List(3,4)  //组合成新的list
List(1,2)++Set(3,4)  //追加另一个集合
List(1,2,3,2).drop(2)  //删除前n个元素
List(List(1,2),List(3,4)).flatten
List(1,2,3).partition((i:Int)=>i<2)  //划分为两个列表
List(1,2,3,3).slice(1,3)  //返回列表的一部分
List(1,2,3).splitAt(2)   //在指定索引处切分列表
List(1,2).zip(List(3,4))  //返回包含相应元素的元组列表

//在集合中使用占位符_，表示列表的各项元素
List(1,2,3).collect{
  case 1=>"ok"  //List(ok)，返回保留符合条件的元素
}

List("a,b").flatMap(_.split(","))  //将结果列表扁平化
List(1,2,3).product  //列表元素相乘

def boolRe(l:List[Int],start:Boolean)(f:(Boolean,Int)=>Boolean):Boolean={
  var s=start
  for(i<-l) s=f(s,i)
  s
}
//传入匿名函数,使用大括号括起来
boolRe(List(1,2,3),false){
  (a,j)=>if(a) a else(j==2)
}

//使用类型参数
def op[A,B](l:List[A],s:B)(f:(B,A)=>B):B={
  var a=s
  for(i<-l) a=f(a,i)
  a
}
val leix=op(List(1,2,3),false){(a,i)=>{
  if(a) a else (i==10)
}}

//列表折叠
//给定起始值，再进行规约
List(1,2,3).fold(1){
  (i,j)=>i+j
}
//直接从第一个元素规约列表
List(1,2,3).reduce(_+_)
//给定起始值，各个累加值的列表，逐渐累加
List(1,2,3).scan(0)(_+_)

val baohan=List(1,2,3).foldLeft(false){
  (a,i)=>if(a) a else (i==2)
}

//将不可变集合转换为可变的集合ArrayBuffer
List(1,2,2,3).toBuffer
//转换为set集合，可以去重
List(1,2,2,3).toSet

//java和scala的集合互相转换
import collection.JavaConverters._
//类型为java.util.List[Int]
List(1,2).asJava
//类型为mutable.Buffer[Int]
new util.ArrayList[Int](1).asScala

//元组的模式匹配
("a",1,true) match {
  case (_,_,false)=>1
  case ("a",_,false)=>2
  case (a,b,true)=> println(a,b)    //值绑定
}

//可变集合，注意List将转变为可变序列Buffer
import scala.collection.mutable._
//不可变集合和可变集合转换
val map=Map("a"->1,"b"->2)
//ArrayBuffer((b,2), (a,1))，Map转换为元组，mutable.Buffer[(String, Int)]
val kebian=map.toBuffer
kebian.trimStart(1)
kebian+=("c"->3)
//immutable.Map[String,Int],toList,toMap,toSet
val bubian=kebian.toMap

//为特定的集合创建构建器,并指定集合元素类型
val goujian=Set.newBuilder[Char]
//添加单个元素
goujian+='a'
//添加多个元素
goujian++=List('b','c','b')
//调用result方法，转换为最终的Set类型
goujian.result()

///ArrayBuffer类型
val sq=Seq(1,2,3)

/**
  * 流Stream是个lazy集合，由一个或多个起始元素和一个递归函数生成。只有在访问元素时才会生成这个元素
  * Stream包含一个表头和表尾
  */
def inc(i:Int):Stream[Int]=Stream.cons(i,inc(i+1))
//Stream(1, ?)，表示流只包含起始值1但将来会有其他值
inc(1)
//通过take获取接下来的元素。List(2, 3, 4, 5)
inc(2).take(4).toList

//Option集合，包含两个子类型Some和None
/*
Some是一个类型参数化的单元素集合
None是空集合
Option()可以检测null值
 */
//Option的null判断
Option(null).isEmpty

def divide(a:Int,b:Int):Option[Double]={
  if (b==0) None
  else Option(a/b)
}
divide(3,0)

//filter操作和map操作都是类型安全，不会导致空指针异常，返回None
Some("as").filter(i=>i.size>5).map(_.size)

//Try集合将错误处理转变为集合管理。捕获给定函数参数中发生的错误，并返回这个错误，
//否则如果函数成功则返回结果
/*
1.throw抛出异常，中断整个循环
2.使用try{}...catch{}块
3.使用util.Try，建议使用。可以使用类似Option的操作
 */
def loop (a:Int,b:Int):Int={
  for(i<-1 to a){
    println(i)
    if(i==b) throw new Exception("--------")
  }
  a
}
//loop(4,3)
try{
  loop(3,2)
}catch{
  //捕获抛出的异常
  case e:Exception=>println(e)
}
//Success(2)
// Failure(java.lang.Exception: --------)

Try(loop(4,3)).getOrElse("dd")

//把Failure变为Success,再使用模式匹配
Try(" 123".toInt).orElse(Try(" 123".trim.toInt)) match {
  case Success(x)=>Some(x)
  case Failure(y)=>println(y)
}

//Future集合，发起后台任务，类似Option和Try
//调用Future并提供一个函数会在一个单独的线程中执行函数，而当前线程仍继续操作
//在创建future前必须指定当前会话或应用的上下文来并发运行函数
import concurrent.ExecutionContext.Implicits.global
val fut=Future{
  println("hello")
}

val sleep=Future{
  Thread.sleep(2000)
  println("hi")
}
println("waiting")
