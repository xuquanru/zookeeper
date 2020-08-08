package org.example;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;


/**
 * 🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌道阻且长，行则将至🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌
 * 🍁 Program: zookeeper
 * 🍁 Description
 * 🍁 Author: Stephen
 * 🍁 Create: 2020-08-08 21:24
 * 🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌行而不辍，未来可期🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌
 **/
public class TestConfig {

    @Test
    public void getConfig()  {
        ZooKeeper zk = ZKUtils.getZK();

        WatchCallBack watchCallBack = new WatchCallBack();
        watchCallBack.setZooKeeper(zk);

        MyConf myConf = new MyConf();
        watchCallBack.setMyConf(myConf);

        watchCallBack.await();//阻塞
        while (true) {
            if(myConf.getConf().isEmpty()){
                System.err.println("empity");
                watchCallBack.await();//配合countdowan
            }else{
                System.err.println(myConf.getConf());
            }


            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
