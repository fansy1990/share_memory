package com.share.memory.zk.source_code_server;

import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import com.share.memory.utils.ClassPathFileProcess;

/**
 * author : fanzhe
 * email : fansy1990@foxmail.com
 * date : 2019/10/19 AM10:57.
 */
public class QuorumPeerConfigExtend extends QuorumPeerConfig {
    private static final Logger LOG = LoggerFactory.getLogger(QuorumPeerConfigExtend.class);

    @Override
    public void parse(String path) throws ConfigException{
        LOG.info("Reading configuration from: " + path);

        try {
            Properties cfg =
             ClassPathFileProcess.readClasspathFile2Properties(path);
            parseProperties(cfg);
        } catch (IOException e) {
            throw new ConfigException("Error processing " + path, e);
        } catch (IllegalArgumentException e) {
            throw new ConfigException("Error processing " + path, e);
        }
    }


}
