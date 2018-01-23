package kryo

import java.io._

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}
import org.objenesis.strategy.StdInstantiatorStrategy

import scala.collection.mutable.Map

case class Simple(name:String,age:Int,map:Map[String,Int])
  extends Serializable


object KryoDemo {

  def setSerializableObject: Unit = {
    val kryo=new Kryo()
    kryo.setReferences(false)
    kryo.setRegistrationRequired(false)
    kryo.setInstantiatorStrategy(new StdInstantiatorStrategy())
    kryo.register(classOf[Simple])

    val fo=new FileOutputStream("data/KryoSerializable.txt")
    val oo=new Output(fo)
    for(i<- 0 to 10000){
      var map=Map[String,Int]()
      map+=("wang"->i)
      map+=("zhiqun"->i)
      kryo.writeObject(oo,Simple("name",i+1,map))
    }
    oo.flush()
    oo.close()
  }

  def getSerializableObject: Unit = {
    val kryo=new Kryo()
    kryo.setReferences(false)
    kryo.setRegistrationRequired(false)
    kryo.setInstantiatorStrategy(new StdInstantiatorStrategy())

    val fs=new FileInputStream("data/KryoSerializable.txt")
    val oi=new Input(fs)
    kryo.readObject(oi,classOf[Simple])
  }


  def main(args: Array[String]): Unit = {

    //kryo是java原生序列化性能十几倍

    var start=System.currentTimeMillis()
    setSerializableObject
    println("kryo序列化时间:"+(System.currentTimeMillis() - start) + " ms" )

    start=System.currentTimeMillis()
    getSerializableObject
    println("kryo反序列化时间:"+(System.currentTimeMillis() - start) + " ms" )
  }
}
