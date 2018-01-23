package com.wzq.scala.operation;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FailFastTest {
    public static void main(String[] args){
        Hashtable<String, String> table = new Hashtable<String, String>();
        table.put("a", "aa");
        table.put("b", "bb");
        table.put("c", "cc");
        table.remove("c");
        Iterator<Map.Entry<String, String>> iterator = table.entrySet().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().getValue());
            //采用iterator直接进行修改 程序正常
            iterator.remove();
            //直接从hashtable增删数据就会报错,在迭代过程中增减了数据，就是快速失败
            //table.put("d", "dd");
        }
        System.out.println("-----------");

        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        map.put("a", "aa");
        map.put("b", "bb");
        map.put("c", "cc");
        Iterator<Map.Entry<String, String>> mapiterator = map.entrySet().iterator();
        while (mapiterator.hasNext()) {
            System.out.println(mapiterator.next().getValue());
            map.remove("c");// 正常 并发集合不存在快速失败问题
            map.put("d", "dd");// 正常 并发集合不存在快速失败问题
        }

    }
}
