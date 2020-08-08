package org.example;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{

    public static final String ClustIP = "120.78.177.100:2181,120.25.172.195:2181,111.231.106.4:2181";
    

    public static void main(String[] args ) throws IOException, InterruptedException, KeeperException {
        //zk 有session概念，没有线程池概念
        //watch分两类，一类是new zookeeper传入的watch,session级别的，node是无关的，集群发生变化收不到通知
        //watch 只发生在读调用
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(ClustIP, 10000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                //依赖事件回调的
                Event.KeeperState state = watchedEvent.getState();
                Event.EventType type = watchedEvent.getType();//什么事件调了你
                String path = watchedEvent.getPath();

                System.err.println("new zk watch --default "+watchedEvent.toString());
                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.err.println("连接成功");
                        countDownLatch.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                }

                switch (type) {
                    case None:
                        break;
                    case NodeCreated:
                        break;
                    case NodeDeleted:
                        break;
                    case NodeDataChanged:
                        break;
                    case NodeChildrenChanged:
                        break;
                }
            }
        });
        countDownLatch.await();
        ZooKeeper.States state = zooKeeper.getState();
        switch (state) {
            case CONNECTING:
                System.err.println("in...");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.err.println("connected");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        //创建节点
        String pathNames = zooKeeper.create("/ooxx", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        Stat stat = new Stat();
        byte[] node = zooKeeper.getData("/ooxx", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.err.println("getData>" + event.toString());
                try {
                    //true 是 default,stat 版本是0
                    zooKeeper.getData("/ooxx", true, stat);//在生成一个watch
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);
        System.err.println(node.toString());

        //设置数据
        Stat stat1 = zooKeeper.setData("/ooxx", "newdata".getBytes(), 0);

        Stat stat2 = zooKeeper.setData("/ooxx", "newdata01".getBytes(), stat1.getVersion());


        //测试异步数据
        System.err.println("-----------async-----start--------");
        zooKeeper.getData("/ooxx", false,
                new AsyncCallback.DataCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                        //rc 是状态码，ctx 是上下文，类标识
                        System.err.println("-----------async-----callback--------");
                        System.err.println(new String(data));
                    }
                }, "abc");

        System.err.println("-----------async-----over--------");

        TimeUnit.SECONDS.sleep(3000);//先阻塞住，再去看看服务器的节点状态

    }


}
