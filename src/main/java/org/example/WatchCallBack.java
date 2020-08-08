package org.example;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌道阻且长，行则将至🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌
 * 🍁 Program: zookeeper
 * 🍁 Description
 * 🍁 Author: Stephen
 * 🍁 Create: 2020-08-08 21:57
 * 🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌行而不辍，未来可期🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌🐌
 **/
public class WatchCallBack implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {
    ZooKeeper zooKeeper;
    MyConf myConf;
    CountDownLatch downLatch = new CountDownLatch(1);

    public MyConf getMyConf() {
        return myConf;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void process(WatchedEvent event) {
        //节点可能被修改
        switch (event.getType()) {

            case None:
                break;
            case NodeCreated:
//                节点被创建需要
                zooKeeper.getData("/AppConf", this, this, "suibian");
                break;
            case NodeDeleted:
                myConf.setConf("");
                downLatch=new CountDownLatch(1);//配合config
                break;
            case NodeDataChanged:
                zooKeeper.getData("/AppConf", this, this, "suibian");
                break;
            case NodeChildrenChanged:
                break;
        }

    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat != null) {
            //如果存在就会执行这个方法，而这个方法又会导致带有data方法执行
            System.err.println(ctx);
            zooKeeper.getData("/AppConf", this, this, "suibian");
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if (data != null) {
            String s = new String(data);
            myConf.setConf(s);
            downLatch.countDown();
        }
    }

    public void await() {
        zooKeeper.exists("/AppConf",
                this, this, "ABC");
        try {
            downLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
