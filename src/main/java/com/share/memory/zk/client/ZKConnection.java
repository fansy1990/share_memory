package com.share.memory.zk.client;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * author : fanzhe
 * email : fansy1990@foxmail.com
 * date : 2019/10/20 AM9:28.
 */
public class ZKConnection {
    private static final Logger LOG = LoggerFactory.getLogger(ZKConnection.class);
    private ZooKeeper zoo;
    CountDownLatch connectionLatch = new CountDownLatch(1);


    public ZooKeeper connect(String connectString) throws IOException, InterruptedException {
        LOG.info("Zookeeper connect string : {}", connectString);
        zoo = new ZooKeeper(connectString, 2000, new Watcher() {
            public void process(WatchedEvent we) {
                if (we.getState() == Event.KeeperState.SyncConnected) {
                    connectionLatch.countDown();
                }
            }
        });

        connectionLatch.await();
        return zoo;
    }

    public void close() throws InterruptedException {
        zoo.close();
    }
}