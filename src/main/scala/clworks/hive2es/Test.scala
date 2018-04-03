package clworks.hive2es

object Test {
  def main(args: Array[String]): Unit = {

    val m=Map("a"->1,"b"->2)
    val m2=Map("id"->1)
    EsConfs.sc.parallelize(Seq(m))

  }
}
