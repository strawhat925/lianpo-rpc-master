package com.lianpo.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2017/1/5.
 */
public class Environment {
    private static final Logger logger = LoggerFactory.getLogger(Environment.class);

    public static final String ZK_ADDRESS = "192.168.20.30";

    public static final String ZK_SERVICE_PATH = "";

    public static final String FRAMEWORK_NAME = "lianpo";

    /**
     * get localhost address
     */
    public static String getLocalhostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            logger.error("Get ip address failed", e.getMessage());
        }
        return null;
    }
}
