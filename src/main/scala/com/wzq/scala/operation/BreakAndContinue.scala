package com.wzq.scala.operation

import scala.io.Source
import scala.util.control.Breaks._

object BreakAndContinue {
  def main(args: Array[String]): Unit = {

    /**
      * breakable在for循环里面，相当于continue
      * breakable在for循环外面，相当于break
      */

    //continue使用
    for(i<- 0 to 10){
      breakable{
        if(i%2==0) break()
        println(i)
      }
    }

    println("------------")

    //break使用
    breakable{
      for(i<- 0 to 10){
        if(i%2==1) break()
        println(i)
      }
    }

    println("------------")
    val words=Source.fromFile("data/word.txt").getLines()

/*continue
    while(words.hasNext){
     breakable{
       val word=words.next()
       if(word.contains("d")) break()
       println(word)
     }
    }*/

    //break
    breakable{
      while(words.hasNext){
        val word=words.next()
        if(word.contains("d")) break()
        println(word)
      }
    }

    while (true){
      for (i<- 0 to 1)println(i)
    }

  }
}
