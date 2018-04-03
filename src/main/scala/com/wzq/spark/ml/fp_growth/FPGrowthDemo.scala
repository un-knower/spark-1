package com.wzq.spark.ml.fp_growth

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.sql.SparkSession

object FPGrowthDemo {
  def main(args: Array[String]): Unit = {

    /**
      * 1.org.apache.spark.SparkException: Items in a transaction must be unique
      * 错误原因：每条items必须去重
      * 2.org.apache.spark.shuffle.FetchFailedException: Too large frame:
      * 错误原因：支持度设置过小，使得产生大量的频繁项集，超出内存。调节设置合理的支持度
      */


    val ss=SparkSession.builder().appName("fp").master("local[3]")
      .getOrCreate()
    val sc=ss.sparkContext
    import ss.implicits._
    Logger.getLogger("org").setLevel(Level.ERROR)
   /* val data=MySparkConfs.sc.textFile("data/fp_growth.txt")
    val transactions=data.map(_.trim.split(" "))
    val fpg=new FPGrowth().setMinSupport(0.2).setNumPartitions(10)
    val model=fpg.run(transactions)*/
/*    //查出所有的频繁项集，并且列出次数
    model.freqItemsets.collect().foreach{itemset=>
      println(itemset.items.mkString("[",",","]")+","+itemset.freq)
    }
    //根据置信度筛选关联规则
    val minConfidence=0.8
    //前项，后项，置信度
    model.generateAssociationRules(minConfidence).collect().foreach{rule=>
      println(
        rule.antecedent.mkString("[",",","]")
        +"=>"+rule.consequent.mkString("[",",","]")
        +","+rule.confidence
      )
    }
    //查看规则生成的数量
    println(model.generateAssociationRules(minConfidence).collect().length)*/



/*    val data=sc.parallelize(List(
      "1,2,5","1,2,3,5","1,2"
    )).toDF("items")
    val transactions=data.rdd.map(i=>(i(0).toString.trim.split(",")))
    val fpg=new FPGrowth().setMinSupport(0.5).setNumPartitions(8)
    val model=fpg.run(transactions)
    val freqItemSets = model.freqItemsets.map { itemset =>
      val items=itemset.items.mkString(",")
      val freq=itemset.freq
      (items,freq)
    }.toDF("items","freq")
    freqItemSets.show()
    val minConfidence=0.6
    val rules=model.generateAssociationRules(minConfidence)
    val df=rules.map{s=>
      val left=s.antecedent.mkString(",")
      val right=s.consequent.mkString(",")
      val confidence=s.confidence
      (left,right,confidence)
    }.toDF("left_collect", "right_collect", "confidence")
    df.show()*/

    /**
      * 1.获取商品信息
      * 2.FPGrowth模型建立
      * 3.设置setMinSupport为0.05，不满足该支持度的数据将被去除
      * 4.获取满足支持度条件的频繁项集
      */
    val data=sc.textFile("data/fp_growth.txt")
    val dataNoHead=data.filter(line=> !line.contains("items"))
    val dataS=dataNoHead.map(line=>line.split("\\{"))
    val dataGoods=dataS.map(s=>s(1).replace("}\"",""))
    val fpData=dataGoods.map(_.split(",")).cache()
    val fpgGroup=new FPGrowth().setMinSupport(0.05).setNumPartitions(8)
    val fpModel=fpgGroup.run(fpData)
    val freqItems=fpModel.freqItemsets.collect()
    freqItems.foreach{f=>
      println("frequentItems:"+f.items.mkString(",")+":"+"occurrenceFrequency:"+f.freq)
    }

    /**
      * 1.对用户进行商品推荐
      * 2.在频繁项中找出该商品出现的次数，用于后面计算商品之间的置信度
      * 3.设置置信度。当商品的置信度大于这个阈值，就将其推荐给用户。在推荐过程中需要去除用户已经购买了的商品。
      * 4.当推荐商品没有的时候，将频繁项集里面的出现次数最高的几件商品推荐给用户。
      */
    val userId=2
    val usrList=fpData.take(3)(userId)
    var goodsFreq=0L
    for(goods<- freqItems){
      if(goods.items.mkString == usrList.mkString){
        goodsFreq=goods.freq
      }
    }
    println("goodNumber:"+goodsFreq)

    for(f<-freqItems){
      //在频繁项集中选择包含该用户的商品，并且数量大于该用户原始商品数量
      if(f.items.mkString.contains(usrList.mkString) && f.items.size>usrList.size){
        //置信度计算：共同出现的次数/单个出现的次数
        val confidence=f.freq.toDouble/goodsFreq.toDouble
        if(confidence>0.1){
          var item=f.items
          for(i<-0 until(usrList.size)){
            item=item.filter(_ !=usrList(i))
          }
          for(str<- item){
            println(str+"=>"+confidence)
          }
        }
      }
    }

    /**
      * 1.通过置信度筛选出强规则
      * 2.antecedent表示前项
      * 3.consequent表示后项
      * 4.confidence表示规则的置信度
      */
    val minConfidence=0.5
    val rules=fpModel.generateAssociationRules(minConfidence)
    val df=rules.map{s=>
      val left=s.antecedent.mkString(",")
      val right=s.consequent.mkString(",")
      val confidence=s.confidence
      (left,right,confidence)
    }.toDF("left_collect", "right_collect", "confidence")
    df.show(false)

  }
}
