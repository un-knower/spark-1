package com.wzq.scala.operation

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by wangzhiqun on 2017/8/18.
  */
class DateFormat {
  def getDate():SimpleDateFormat= {
    val date=new Date()
    val sdf=new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    sdf
  }
}
