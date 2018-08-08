package com.guzhandong.springframework.boot.elasticsearch.config;

import com.guzhandong.springframework.boot.elasticsearch.client.ElasticsearchClientConfigure;
import com.guzhandong.springframework.boot.elasticsearch.client.RestHighLevelClient;
import com.guzhandong.springframework.boot.elasticsearch.pool.ElasticsearchClientFactory;
import com.guzhandong.springframework.boot.elasticsearch.pool.ElasticsearchClientPool;
import com.guzhandong.springframework.boot.elasticsearch.pool.ElasticsearchClientPoolConfigure;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ElasticsearchClientPool.class, GenericObjectPool.class, org.elasticsearch.client.RestHighLevelClient.class})
public class HighLevelClientAutoConfigure {

    @Bean
    @ConfigurationProperties(prefix = ElasticsearchClientConfigure.PREFIX)
    @ConditionalOnProperty(prefix = ElasticsearchClientConfigure.PREFIX,value = {"hosts"})
    @ConditionalOnMissingBean(ElasticsearchClientConfigure.class)
    public ElasticsearchClientConfigure elasticsearchClientConfigure(){
        return new ElasticsearchClientConfigure();
    }


    @Bean
    @ConfigurationProperties(prefix = ElasticsearchClientPoolConfigure.PREFIX)
    @ConditionalOnMissingBean(ElasticsearchClientPoolConfigure.class)
    public ElasticsearchClientPoolConfigure elasticsearchClientPoolConfigure(){
        ElasticsearchClientPoolConfigure elasticsearchClientPoolConfigure = new ElasticsearchClientPoolConfigure();
        //TODO  开启jmx 导致  springboot（version:2.0.1，内嵌tomcat，和euraka集成），启动后(erueka 注册日志:registration status: 404)立即关闭servlet 容器，暂时默认设置jmx为关闭状态，再寻找原因
        elasticsearchClientPoolConfigure.setJmxEnabled(false);

        return elasticsearchClientPoolConfigure;
    }


    @Bean
    @ConditionalOnBean(ElasticsearchClientConfigure.class)
    @ConditionalOnSingleCandidate(ElasticsearchClientConfigure.class)
    @ConditionalOnMissingBean(ElasticsearchClientFactory.class)
    public ElasticsearchClientFactory elasticsearchClientFactory(
            @Autowired ElasticsearchClientConfigure elasticsearchClientConfigure) {
        return new ElasticsearchClientFactory(elasticsearchClientConfigure);
    }



    @Bean
    @ConditionalOnBean({ElasticsearchClientFactory.class,ElasticsearchClientPoolConfigure.class})
    @ConditionalOnMissingBean(ElasticsearchClientPool.class)
    public ElasticsearchClientPool remoteExecConnectionPool(
            @Autowired ElasticsearchClientFactory elasticsearchClientFactory,
            @Autowired ElasticsearchClientPoolConfigure elasticsearchClientPoolConfigure) {
        return new ElasticsearchClientPool(elasticsearchClientFactory,elasticsearchClientPoolConfigure);
    }

    @Bean
    @ConditionalOnBean({ElasticsearchClientPool.class})
    @ConditionalOnMissingBean(RestHighLevelClient.class)
    public RestHighLevelClient restHighLevelClient(
            @Autowired ElasticsearchClientPool elasticsearchClientPool) {
        return new RestHighLevelClient(elasticsearchClientPool);
    }
}
