//重写默认的toString方法
class User{
  //显示实例内容
  override def toString: String = "hi"
}

//类参数,在类名后定义，可以用于初始化字段，或者将某个字段声明为类参数（加上val或者var）
//类参数也可以定义默认值
class User2(name:String){
  override def toString: String = "hi"
}
class User3(val name:String){
  override def toString: String = "hi"
}
val u=new User3("wang")
//加上val的类参数相当于类中的一个字段
u.name

//this访问当前类的字段和方法，super访问父类的字段和方法

//有类型参数的类,类型参数确定元素及操作的类型
class Singu[A](s:A) extends Traversable[A]{
   def foreach[U](f: (A) => U) = f(s)
}

new Singu("hello")

//抽象类abstract，不能实例化，在抽象类中定义核心字段和方法，但不提供具体实现，也可以提供实现的字段和方法
abstract class Car{
  val year:Int
  def color:String
}
//使用类参数初始化字段
class  Moto (val year:Int) extends Car{
  override def color: String = ""
}

//匿名类，如果子类只需要使用一次。可以实例化父类，并在类名参数后面加上大括号，包含具体实现
val bike=new Car {override def color: String = ""

  override val year: Int = 1
}

//apply方法，直接调用而不需要方法名,是一个默认方法
class App(a:Int){
  def apply(b: Int) = a*b
}
//等价
val a=new App(2)
a(3)  //<==>
a.apply(3)

//懒值，只在第一次调用该值时才会执行，在类的生命周期只执行一次
class  Laz{
  val a={println("changgui")}
  lazy val b={println("lanzhi")}
}
new Laz().b

//protected：限制只有同一个类或子类的代码才能访问字段或者方法
//sealed类，密封类，限制一个类的子类必须位于父类所在的同一个文件中。比如option类，确保不存在其他子类

/*
Object对象，是一个类类型，只能有不超过1个实例，又叫单例。不需要用new创建实例，直接按名访问对象
字段或方法为静态或全局（static）
Object中不能有参数，自动实例化，但可以定义字段、方法和内部类
 */
object Hello{
  println("hello")
  def hi="hi"
}
//多次调用对象的hi方法会重用一个全局实例，不会额外初始化
Hello.hi
Hello.hi

//伴生对象，与类同名的一个对象，与类在同一个文件定义，可以互相访问私有和保护字段、方法
//可以从伴生对象生成一个类的新实例，使用apply
object User{
  def apply: User = new User()
}

//case类，不可实例化的类，包含多个自动生成的方法，自动生成伴生对象，这个对象也有自己的自动生成的方法

//Trait类，支持多重继承，不能实例化 。使用with扩展多个trait
//先扩展类再使用with关键字增加trait。如果指定了父类，父类必须放在所有父trait前面
trait Utils{
  def remove(s:String)={
    s.replaceAll("[0-1]","sss")
  }
}
trait Safe{
  def trimto(s:String):Option[String]={
    Some(s).map(_.trim).filterNot(_.isEmpty)
  }
}

class Page (val s:String) extends  Safe with Utils{
  def plain:String={
    trimto(s).map(remove).getOrElse("n/a")
  }
}

/*
多重继承实质上是编译器创建各个trait的副本，形成类和trait组成的单列层次体系，构成垂直链
多重继承顺序为从右到左（从最低的子类到最高基类），类定义class D extends A with B with C
将由编译器重新实现为class D extends C extends B extends A，最右边的trait是所定义类的直接父类，
第一个trait是最后一个父类
 */

/*
自类型self，trait注解，向一个类增加该trait时，要求这个类必须有一个特定的类型或子类型
 */

import util.Random._
alphanumeric.take(20).mkString(",")

/*
隐含类，类型安全的，可以为现有的类增加新方法和字段，从原类自动转换为新类，可以在原类上直接调用隐含类的方法和字段
在一个实例上访问未知的字段或方法时，会使用隐含转换。在当前命名空间中寻找匹配
在使用隐式转换时，必须将隐含类增加到命名空间,使用import
 */

object Impli{
  /*
  隐含类必须至少一个非隐含类参数
  隐含类必须在另一个对象、类或trait中定义，且类名不冲突
   */
  implicit class Hello(s:String){def he="hello,"+s}
}
import Impli._
Impli.Hello("a")

/*
隐含参数，定义在单独的参数组
调用者在自己的命名空间提供默认值，调用函数时，可以为隐含参数显示指定值
如果没有显示指定，会使用局部隐含值，增加到函数调用
 */

object Dly{
  def p(n:Double)(implicit s:String)={
    println(s.format(n))
  }
}
//显示指定
Dly.p(2.325)("%1f")

//使用局部隐含值,自动填充隐含参数
case  class US(amount:Double){
  implicit val pri="%1f"
 // implicit val pr2="%2d"
  def prin=Dly.p(amount)
}
new US(1.242).prin

//元组实质为TupleX[Y] case类的实例，X表示输入参数的个数
Tuple4

//匿名函数实质为FunctionX[Y]的实例
//Int => Int = <function1>
(a:Int)=>a+1

/*
定义新的类型别名：使用type
只能在对象、类、trait中定义,当在trait中定义时，必须在子类中实现
 */
object Ty{
  type Mytype=Int
  val a:Mytype=1
  type U=Tuple2[Int,Int]
  val b:U=(1,2)
  type T[A,B,C]=Tuple3[A,B,C]
  val t=(1,"a",2)
}

//两种方法定义类型参数
trait Aa{
  type A
  def cr:A
}
trait Bb[A]{
  def cr:A
}
class Aaa extends Aa{
  type A=Int

  override def cr: Int = {
    2
  }
}

/*
定界类型限制，只能是一个特定的类或者它的子类或基类型
上界：限制一个类型只能是该类型或者它的某个子类型。操作符'<:'
下界：限制一个类只能是该类型或者它扩展的某个基类型。操作符'>:'
 */
class BaseUser(val name:String)
class BaseUser2( name:String) extends BaseUser(name)
class BaseUser3( name:String) extends BaseUser2(name)

//传入参数类型只能是BaseUser或者它的子类
def cust[A<:BaseUser](c:A): Unit ={
  println("shangkjie")
}
cust(new BaseUser2("ssss"))

//
def on[B>:BaseUser2](o:BaseUser2):B= o match {
  case a:BaseUser2=>new BaseUser2(o.name)
  case b:BaseUser3=>new BaseUser3(o.name)
}
val base1=on(new BaseUser2("aa"))
val base2=on(new BaseUser3("bb"))

