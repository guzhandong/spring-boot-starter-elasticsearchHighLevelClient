package com.guzhandong.springframework.boot.elasticsearch.pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticsearchClientPool extends GenericObjectPool<RestHighLevelClient> {

    public ElasticsearchClientPool(PooledObjectFactory factory) {
        super(factory);
    }

    public ElasticsearchClientPool(PooledObjectFactory factory, GenericObjectPoolConfig config) {
        super(factory, config);
    }

    public ElasticsearchClientPool(PooledObjectFactory factory, GenericObjectPoolConfig config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
