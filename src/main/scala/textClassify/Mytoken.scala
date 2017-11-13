package textClassify

import org.apache.spark.ml.UnaryTransformer
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{ArrayType, DataType, StringType}

/**
  * 继承UnaryTransformer，可以实现新添列
  * 对原有的列应用匿名函数，做一些处理
  * @param uid
  */

/*class Mytoken(override val uid:String)  extends UnaryTransformer[String, Seq[String], Mytoken]{

  def this() = this(Identifiable.randomUID("mytok"))

  override protected def createTransformFunc: (String) => Seq[String] = {
    _.toLowerCase.split("\\s")
  }
  override protected def validateInputType(inputType: DataType): Unit = {
    require(inputType == StringType, s"Input type must be string type but got $inputType.")
  }

  override protected def outputDataType: DataType = new ArrayType(StringType, true)

  override def copy(extra: ParamMap): Mytoken = defaultCopy(extra)
}*/

class Mytoken(override val uid:String)  extends UnaryTransformer[String,String, Mytoken]{

  def this() = this(Identifiable.randomUID("mytok"))

  override protected def createTransformFunc: (String) => String = {
    i=>
      try{
        i.toLowerCase().reverse
      }catch {
        case e:Exception=>e.toString
      }
  }
  override protected def validateInputType(inputType: DataType): Unit = {
    require(inputType == StringType, s"Input type must be string type but got $inputType.")
  }

  override protected def outputDataType: DataType =  StringType

  override def copy(extra: ParamMap): Mytoken = defaultCopy(extra)
}

object  MyTest{
  def main(args: Array[String]): Unit = {
    val ss=SparkSession.builder().appName("bayes").master("local[2]")
      .getOrCreate()
    import ss.implicits._

    val bayesDF=ss.read.csv("data/sms_spam.csv")
      .toDF("label","sentence")
    val tok=new Mytoken()
      .setInputCol("sentence")
      .setOutputCol("message")
    tok.transform(bayesDF).show(20,false)
  }
}
