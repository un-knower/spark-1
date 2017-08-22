package com.wzq.scala.operation

import java.text.SimpleDateFormat
import java.util.Date

class DateFormat {
  def getDate():SimpleDateFormat= {
    val date=new Date()
    val sdf=new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    sdf
  }
}
