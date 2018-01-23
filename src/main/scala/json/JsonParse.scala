package json

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

object JsonParse {
  def main(args: Array[String]): Unit = {
/*    val json=("name"->"tom")
    println(compact(render(json)))*/

    val json = ("name" -> "joe")~("age" -> 35)
    println(compact(render(json)))

  }
}
