package clworks

import org.apache.spark.ml.UnaryTransformer
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.types.{DataType, StringType}

class MobileClassifyN(override val uid:String)
  extends UnaryTransformer[String, String, MobileClassifyN] {

  def this() = this(Identifiable.randomUID("MobileClassifyN"))

  override protected def createTransformFunc: (String) => String= {
    s=>
      try{
        s.replace(s,"0")
      }catch {
        case e:Exception=>e.toString
      }
  }

  override protected def validateInputType(inputType: DataType): Unit = {
    require(inputType == StringType, s"Input type must be string type but got $inputType.")
  }

  override protected def outputDataType: DataType = StringType

  override def copy(extra: ParamMap): MobileClassifyN = defaultCopy(extra)
}
