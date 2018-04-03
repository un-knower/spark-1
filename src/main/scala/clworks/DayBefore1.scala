package clworks

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

object DayBefore1 {
  def main(args: Array[String]): Unit = {
    val sdf=new SimpleDateFormat("yyyyMMdd")
    val now=new Date()
    val calendar=Calendar.getInstance()
    calendar.setTime(now)
    calendar.add(Calendar.DAY_OF_MONTH,-1)  //获取当前日期前一天
    val nowBefore1=calendar.getTime
    println(sdf.format(nowBefore1))

  }
}
