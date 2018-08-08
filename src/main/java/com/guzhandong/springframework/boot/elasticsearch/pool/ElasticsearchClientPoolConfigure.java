package com.guzhandong.springframework.boot.elasticsearch.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class ElasticsearchClientPoolConfigure extends GenericObjectPoolConfig {
    public static final String PREFIX = "spring.es.pool";
}
