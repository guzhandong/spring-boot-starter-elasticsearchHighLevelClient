package com.guzhandong.springframework.boot.elasticsearch.pool;

import com.guzhandong.springframework.boot.elasticsearch.client.ElasticsearchClientConfigure;
import com.guzhandong.springframework.boot.elasticsearch.client.RestHighLevelClient;

/**
* RemoteExecConnectionFactory Tester. 
* 
* @author <Authors name> 
* @since <pre>八月 5, 2018</pre> 
* @version 1.0 
*/ 
public class RemoteExecConnectionFactoryTest {

    public static void main(String[] args) throws Exception {
        ElasticsearchClientConfigure remoteShellProperties = new ElasticsearchClientConfigure();
        remoteShellProperties.setHosts(new String[]{"192.168.100.104:9200","192.168.100.103:9200"});
        ElasticsearchClientPoolConfigure elasticsearchClientPoolConfigure = new ElasticsearchClientPoolConfigure();
        elasticsearchClientPoolConfigure.setMinIdle(3);
        elasticsearchClientPoolConfigure.setMaxTotal(4);
        elasticsearchClientPoolConfigure.setTestOnBorrow(true);
        elasticsearchClientPoolConfigure.setTestOnCreate(true);
        elasticsearchClientPoolConfigure.setTestOnReturn(true);
        elasticsearchClientPoolConfigure.setBlockWhenExhausted(true);
        elasticsearchClientPoolConfigure.setMinEvictableIdleTimeMillis(1000);
        elasticsearchClientPoolConfigure.setTimeBetweenEvictionRunsMillis(5000);
        ElasticsearchClientPool op = new ElasticsearchClientPool(new ElasticsearchClientFactory(remoteShellProperties), elasticsearchClientPoolConfigure);
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(op);
        System.out.println("created connection count : "+ op.getCreatedCount());

        System.out.println(restHighLevelClient.ping());

        System.out.println("main thread complate");
        op.destroy();
        op.close();

    }



} 
