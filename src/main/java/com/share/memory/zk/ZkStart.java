package com.share.memory.zk;

import com.share.memory.zk.client.ZKOperator;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;

import java.io.IOException;

/**
 * author : fanzhe
 * email : fansy1990@foxmail.com
 * date : 2019/10/19 AM10:54.
 */
public class ZkStart implements Runnable{

    private static final String ZOO_FILE = "zoo.cfg";
    public static ZKOperator zkOperator;
    @Override
    public void run() {
        // start zookeeper
        QuorumPeerMain.startServer(new String[]{ZOO_FILE});
        // start client
        try {
            zkOperator = new ZKOperator(ZOO_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void start(){
        new Thread(new ZkStart()).start();
    }

}
