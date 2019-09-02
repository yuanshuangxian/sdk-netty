package com.fqserver.server;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.fqserver.core.conf.imp.NutJsonConfig;



public class NettyConfig {

    public boolean isSSL = System.getProperty("ssl") != null;

    public String host = "127.0.0.1";
    public int port = isSSL ? 8443 : 8080;

    public int module = 0;

    public int ioBossNum = 5;
    public int ioWorkerNum = 20;
    public int backlog = 1024;
    public int revbuf = 32768;
    public int sndbuf = 65536;
    public int connTimeout = 10000;
    public int soTimeout = 10000;

    public boolean tcpNoDelay = true;
    public boolean keepAlive = true;
    public boolean reuseAddr = true;
    public boolean reusePort = true;

    public boolean useReqObjectPool = false;

    // 是否推送buffer数据到aws firehose, 默认为false
    public boolean isSendToFirehose = false;

    // 是否启动Buffer saveTODB模块, 默认为false
    public boolean isStartSaveSchedule = false;

    // 是否启动Pay Schedule模块, 默认为false
    public boolean isStartPaySchedule = false;

    // 是否启用本地缓存数据, 默认为false
    public boolean isUseLocalCache = false;

    public int initCacheSpanTime = 1800000;

    public String defaultWelcomeInfo = "Hello Server for Netty";

    public String default404Info = "HTTP STATUS 404 Not Found";

    public NettyConfig() {}

    public NettyConfig(String fileName) {

        try {
            if (fileName.endsWith(".js") || fileName.endsWith(".json")) {
                NutJsonConfig config = new NutJsonConfig(fileName, "default-netty");
                initConfig(config);

                return;
            }
        }
        catch (Exception e) {
            System.out.println(fileName + " file load failed, use properties config.");
        }

        initProperties();
    }

    public NettyConfig initProperties() {
        String filePath =  "netty.properties";

        return initProperties(filePath);
    }

    public NettyConfig initProperties(String filePath) {
        try {
            Configuration pc = new PropertiesConfiguration(filePath);
            return initConfig(pc);
        }
        catch (ConfigurationException e) {
            e.printStackTrace();
            return this;
        }
    }

    // public NettyConfig initProperties(Configuration pc) {
    //
    // this.host = pc.getString("netty.host", this.host);
    //
    // this.module = pc.getInt("netty.module", this.module);
    //
    // this.port = this.isSSL ? pc.getInt("netty.ssl", this.port) :
    // pc.getInt("netty.port",
    // this.port);
    //
    // this.ioBossNum = pc.getInt("netty.ioBossNum", this.ioBossNum);
    // this.ioWorkerNum = pc.getInt("netty.ioWorkerNum", this.ioWorkerNum);
    //
    // this.backlog = pc.getInt("netty.SO_BACKLOG", this.backlog);
    // this.revbuf = pc.getInt("netty.SO_RCVBUF", this.revbuf);
    // this.sndbuf = pc.getInt("netty.SO_SNDBUF", this.sndbuf);
    // this.connTimeout = pc.getInt("netty.CONNECT_TIMEOUT_MILLIS",
    // this.connTimeout);
    // this.soTimeout = pc.getInt("netty.SO_TIMEOUT", this.soTimeout);
    // this.keepAlive = pc.getBoolean("netty.SO_KEEPALIVE", this.keepAlive);
    // this.reuseAddr = pc.getBoolean("netty.SO_REUSEADDR", this.reuseAddr);
    //
    // this.useReqObjectPool = pc.getBoolean("netty.use_object_pool",
    // this.useReqObjectPool);
    // this.initCacheSpanTime = pc.getInt("netty.init_cache_span_time",
    // this.initCacheSpanTime);
    //
    // this.defaultWelcomeInfo = pc.getString("netty.welcome",
    // this.defaultWelcomeInfo);
    // this.default404Info = pc.getString("netty.404info", this.default404Info);
    //
    // this.isSendToFirehose = pc.getBoolean("netty.is_send_to_firehose",
    // this.isSendToFirehose);
    //
    // return this;
    // }

    public NettyConfig initConfig(Configuration pc) {

        this.host = pc.getString("netty.host", this.host);

        this.module = pc.getInt("netty.module", this.module);

        this.port = this.isSSL ? pc.getInt("netty.ssl", this.port) : pc.getInt("netty.port",
                                                                               this.port);

        this.ioBossNum = pc.getInt("netty.ioBossNum", this.ioBossNum);
        this.ioWorkerNum = pc.getInt("netty.ioWorkerNum", this.ioWorkerNum);

        this.backlog = pc.getInt("netty.SO_BACKLOG", this.backlog);
        this.revbuf = pc.getInt("netty.SO_RCVBUF", this.revbuf);
        this.sndbuf = pc.getInt("netty.SO_SNDBUF", this.sndbuf);
        this.connTimeout = pc.getInt("netty.CONNECT_TIMEOUT_MILLIS", this.connTimeout);
        this.soTimeout = pc.getInt("netty.SO_TIMEOUT", this.soTimeout);
        this.keepAlive = pc.getBoolean("netty.SO_KEEPALIVE", this.keepAlive);
        this.tcpNoDelay = pc.getBoolean("netty.TCP_NODELAY", this.tcpNoDelay);
        this.reuseAddr = pc.getBoolean("netty.SO_REUSEADDR", this.reuseAddr);

        this.useReqObjectPool = pc.getBoolean("netty.useObjectPool", this.useReqObjectPool);
        this.initCacheSpanTime = pc.getInt("netty.initCacheSpanTime", this.initCacheSpanTime);

        this.defaultWelcomeInfo = pc.getString("netty.welcome", this.defaultWelcomeInfo);
        this.default404Info = pc.getString("netty.404info", this.default404Info);

        this.isSendToFirehose = pc.getBoolean("netty.is_send_to_firehose", this.isSendToFirehose);
        this.isStartSaveSchedule = pc.getBoolean("netty.is_start_save_schedule",
                                                 this.isStartSaveSchedule);
        this.isStartPaySchedule = pc.getBoolean("netty.is_start_pay_schedule",
                                                this.isStartPaySchedule);
        this.isUseLocalCache = pc.getBoolean("netty.is_use_local_cache", this.isUseLocalCache);

        return this;
    }
}