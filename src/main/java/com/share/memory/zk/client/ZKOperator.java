package com.share.memory.zk.client;

import com.share.memory.utils.ClassPathFileProcess;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

/**
 * author : fanzhe
 * email : fansy1990@foxmail.com
 * date : 2019/10/20 AM9:30.
 */
public class ZKOperator {
    private static ZooKeeper zkeeper;
    private static ZKConnection zkConnection;

    public ZKOperator(String zooFile) throws IOException, InterruptedException {
        initialize(zooFile);
    }

    private void initialize(String zooFile) throws IOException, InterruptedException {
        zkConnection = new ZKConnection();

        zkeeper = zkConnection.connect(getConnectString(zooFile));
    }

    private String getConnectString(String zooFile) throws IOException {
        Properties properties = ClassPathFileProcess.readClasspathFile2Properties(zooFile);
        String connectString = "";
        String port = properties.getProperty("clientPort");
        for(Map.Entry<Object,Object> entry: properties.entrySet()){
            String k = entry.getKey().toString().trim();
            if(k.startsWith("server")){
                int dot = k.indexOf('.');
                String sid = k.substring(dot + 1);
                String parts[] = entry.getValue().toString().trim().split(":");
                connectString+= parts[0]+":"+port+",";
            }
        }
        return connectString.substring(0,connectString.length()-1);
    }

    public void closeConnection() throws InterruptedException {
        zkConnection.close();
    }

    public void create(String path, byte[] data) throws KeeperException, InterruptedException {

        zkeeper.create(
                path,
                data,
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
    }

    public Object getZNodeData(String path, boolean watchFlag)
            throws KeeperException,
            InterruptedException, UnsupportedEncodingException {

        byte[] b = null;
        b = zkeeper.getData(path, null, null);
        return new String(b, "UTF-8");
    }

    public void update(String path, byte[] data) throws KeeperException,
            InterruptedException {
        int version = zkeeper.exists(path, true).getVersion();
        zkeeper.setData(path, data, version);
    }

    public void set(String path, String data) throws KeeperException, InterruptedException {
        if(exist(path)){
            update(path, data.getBytes());
        }else{
            create(path, data.getBytes());
        }
    }

    public Object get(String path) throws KeeperException, InterruptedException {
        if(exist(path)){
            return zkeeper.getData(path,false,zkeeper.exists(path,true));
        }
        return null;
    }

    public boolean exist(String node) throws KeeperException, InterruptedException {
        return zkeeper.exists(node, false) !=null ;
    }
}