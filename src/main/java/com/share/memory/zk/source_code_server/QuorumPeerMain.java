package com.share.memory.zk.source_code_server;

/**
 * author : fanzhe
 * email : fansy1990@foxmail.com
 * date : 2019/10/19 AM10:56.
 */

import com.share.memory.utils.ClassPathFileProcess;
import org.apache.zookeeper.jmx.ManagedUtil;
import org.apache.zookeeper.server.*;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;

public class QuorumPeerMain {
    private static final Logger LOG = LoggerFactory.getLogger(org.apache.zookeeper.server.quorum.QuorumPeerMain.class);

    private static final String USAGE = "Usage: QuorumPeerMain configfile";

    protected QuorumPeer quorumPeer;

    /**
     * To start the replicated server specify the configuration file name on
     * the command line.
     * @param args path to the configfile
     */
    public static void startServer(String[] args) {
        QuorumPeerMain main = new QuorumPeerMain();
        try {
            if(!initialMyIdFile(args)){
                return ;
            }
            main.initializeAndRun(args);
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid arguments, exiting abnormally", e);
            LOG.info(USAGE);
            System.err.println(USAGE);
            System.exit(2);
        } catch (QuorumPeerConfig.ConfigException e) {
            LOG.error("Invalid config, exiting abnormally", e);
            System.err.println("Invalid config, exiting abnormally");
            System.exit(2);
        } catch (Exception e) {
            LOG.error("Unexpected exception, exiting abnormally", e);
            System.exit(1);
        }
        LOG.info("Exiting normally");
        System.exit(0);
    }

    protected static boolean initialMyIdFile(String[] args) throws IOException {
        Properties properties = ClassPathFileProcess.readClasspathFile2Properties(args[0]);
        String content = getContent(properties,"server");

        if(content == null){
            LOG.warn("No need to start zookeeper on this host {}", InetAddress.getLocalHost());
            return false;
        }

        File dataDir = new File(getContent(properties,"dataDir"));
        dataDir.deleteOnExit();
        dataDir.mkdirs();

        File myId = new File(dataDir,"myid");
        myId.deleteOnExit();
        myId.createNewFile();
        writeToFile(myId, content);
        return true;
    }

    private static void writeToFile(File file, String words) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(words);
        writer.close();
    }

    private static String getContent(Properties properties , String key) throws UnknownHostException {
        switch (key){
            case "server":
                String hostname = InetAddress.getLocalHost().getHostName();
                for(Map.Entry<Object,Object> entry: properties.entrySet()){
                    String k = entry.getKey().toString().trim();
                    if(k.startsWith("server")){
                        int dot = k.indexOf('.');
                        String sid = k.substring(dot + 1);
                        String parts[] = entry.getValue().toString().trim().split(":");
                        if(hostname.equalsIgnoreCase(parts[0])){
                            return sid;
                        }
                    }
                }
                return null;
            default:
                return properties.getProperty(key);
        }
    }

    protected void initializeAndRun(String[] args)
            throws QuorumPeerConfig.ConfigException, IOException
    {
        QuorumPeerConfig config = new QuorumPeerConfigExtend();
        if (args.length == 1) {
            config.parse(args[0]);
        }

        // Start and schedule the the purge task
        DatadirCleanupManager purgeMgr = new DatadirCleanupManager(config
                .getDataDir(), config.getDataLogDir(), config
                .getSnapRetainCount(), config.getPurgeInterval());
        purgeMgr.start();

        if (args.length == 1 && config.getServers().size() > 0) {
            runFromConfig(config);
        } else {
            LOG.warn("Either no config or no quorum defined in config, running "
                    + " in standalone mode");
            // there is only server in the quorum -- run as standalone
            ZooKeeperServerMain.main(args);
        }
    }

    public void runFromConfig(QuorumPeerConfig config) throws IOException {
        try {
            ManagedUtil.registerLog4jMBeans();
        } catch (JMException e) {
            LOG.warn("Unable to register log4j JMX control", e);
        }

        LOG.info("Starting quorum peer");
        try {
            ServerCnxnFactory cnxnFactory = ServerCnxnFactory.createFactory();
            cnxnFactory.configure(config.getClientPortAddress(),
                    config.getMaxClientCnxns());

            quorumPeer = new QuorumPeer();
            quorumPeer.setClientPortAddress(config.getClientPortAddress());
            quorumPeer.setTxnFactory(new FileTxnSnapLog(
                    new File(config.getDataLogDir()),
                    new File(config.getDataDir())));
            quorumPeer.setQuorumPeers(config.getServers());
            quorumPeer.setElectionType(config.getElectionAlg());
            quorumPeer.setMyid(config.getServerId());
            quorumPeer.setTickTime(config.getTickTime());
            quorumPeer.setMinSessionTimeout(config.getMinSessionTimeout());
            quorumPeer.setMaxSessionTimeout(config.getMaxSessionTimeout());
            quorumPeer.setInitLimit(config.getInitLimit());
            quorumPeer.setSyncLimit(config.getSyncLimit());
            quorumPeer.setQuorumVerifier(config.getQuorumVerifier());
            quorumPeer.setCnxnFactory(cnxnFactory);
            quorumPeer.setZKDatabase(new ZKDatabase(quorumPeer.getTxnFactory()));
            quorumPeer.setLearnerType(config.getPeerType());
            quorumPeer.setSyncEnabled(config.getSyncEnabled());
            quorumPeer.setQuorumListenOnAllIPs(config.getQuorumListenOnAllIPs());

            quorumPeer.start();
            quorumPeer.join();
        } catch (InterruptedException e) {
            // warn, but generally this is ok
            LOG.warn("Quorum Peer interrupted", e);
        }
    }
}
