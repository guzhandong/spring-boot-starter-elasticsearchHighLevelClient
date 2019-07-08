package com.guzhandong.springframework.boot.elasticsearch.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class ElasticsearchClientPoolConfigure extends GenericObjectPoolConfig {
    public static final String PREFIX = "spring.es.pool";
    //初始化
    private static final boolean DEFAULT_CONNECTION_INIT = true;
    private boolean connectionInit = false;

    public ElasticsearchClientPoolConfigure() {
        connectionInit = DEFAULT_CONNECTION_INIT;
    }

    public boolean getConnectionInit() {
        return connectionInit;
    }
    public boolean isConnectionInit() {
        return connectionInit;
    }

    public void setConnectionInit(boolean connectionInit) {
        this.connectionInit = connectionInit;
    }
}
