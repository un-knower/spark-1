package kryo

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

import scala.collection.mutable._

case class Original(name:String,age:Int,map:Map[String,Int])
  extends Serializable

object OriginalSerializable {

  def setSerializableObject: Unit = {
    val fo=new FileOutputStream("data/Serializable.txt")
    val oo=new ObjectOutputStream(fo)
    for(i<- 0 to 10){
      var map=Map[String,Int]()
      map+=("wang"->i)
      map+=("zhiqun"->i)
      oo.writeObject(Original("name",i+1,map))
    }
    oo.flush()
    oo.close()
  }

  def getSerializableObject: Unit = {
    val fs=new FileInputStream("data/Serializable.txt")
    val oi=new ObjectInputStream(fs)
    oi.readObject()
/*    var text:Original=null
    while ((text=oi.readObject().asInstanceOf[Original])!=null){
      println(text.name+text.age)
    }*/

  }

  def main(args: Array[String]): Unit = {
    var start=System.currentTimeMillis()
    setSerializableObject
    println("原生序列化时间:"+(System.currentTimeMillis() - start) + " ms" )

    start=System.currentTimeMillis()
    getSerializableObject
    println("原生反序列化时间:"+(System.currentTimeMillis() - start) + " ms" )

  }
}
