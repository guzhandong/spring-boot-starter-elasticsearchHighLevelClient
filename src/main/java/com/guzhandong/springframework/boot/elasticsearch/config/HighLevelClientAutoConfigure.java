package com.guzhandong.springframework.boot.elasticsearch.config;

import com.guzhandong.springframework.boot.elasticsearch.client.ElasticsearchClientConfigure;
import com.guzhandong.springframework.boot.elasticsearch.client.RestHighLevelClient;
import com.guzhandong.springframework.boot.elasticsearch.pool.ElasticsearchClientFactory;
import com.guzhandong.springframework.boot.elasticsearch.pool.ElasticsearchClientPool;
import com.guzhandong.springframework.boot.elasticsearch.pool.ElasticsearchClientPoolConfigure;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
@ConditionalOnClass({ElasticsearchClientPool.class, GenericObjectPool.class, org.elasticsearch.client.RestHighLevelClient.class})
public class HighLevelClientAutoConfigure {

    private static final Logger LOGGER = LoggerFactory.getLogger(HighLevelClientAutoConfigure.class);

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
        /**
         * 连接池自身在创建时会先创建MXbean,spring 也会注册mxbean，导致：【mxbean 已经存在异常】解决方式如下，当前使用第一种解决方式：
         * 1.这两个冲突，保留一个即可，如下配置禁止spring创建MXbean，因为commons-pool2 本身不会通过 {@link org.apache.commons.pool2.impl.BaseObjectPoolConfig#getJmxEnabled()} 参数判断是否创建mxbean；而spring 会根据该参数决定是否创建mxbean
         * 2.更改spring mxbean 的命名规则，见 {@link org.springframework.jmx.export.MBeanExporter#setNamingStrategy(ObjectNamingStrategy)}  } ,修改对应实现类的生成规则或自定义实现类
         */
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
    public ElasticsearchClientPool elasticsearchClientPool(
            @Autowired ElasticsearchClientFactory elasticsearchClientFactory,
            @Autowired ElasticsearchClientPoolConfigure elasticsearchClientPoolConfigure) {
        return new ElasticsearchClientPool(elasticsearchClientFactory,elasticsearchClientPoolConfigure);
    }

    @Bean
    @ConditionalOnBean({ElasticsearchClientPool.class})
    @ConditionalOnMissingBean(RestHighLevelClient.class)
    public RestHighLevelClient restHighLevelClient(
            @Autowired ElasticsearchClientPoolConfigure elasticsearchClientPoolConfigure,
            @Autowired ElasticsearchClientPool elasticsearchClientPool) {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(elasticsearchClientPool);
        if (elasticsearchClientPoolConfigure.getConnectionInit()) {
            poolConnectionInit(restHighLevelClient);
        }
        return restHighLevelClient;
    }

    /**
     * pool connection init
     */
    public void poolConnectionInit(RestHighLevelClient restHighLevelClient) {
        try {
            boolean pingRes = restHighLevelClient.ping();
            if (pingRes) {
                LOGGER.info("elasticsearch pool connection init ,ping result :{}",pingRes);
            } else {
                LOGGER.warn("elasticsearch pool connection init ,ping result :{}",pingRes);
            }

        } catch (IOException e) {
            LOGGER.warn(e.getMessage(),e);
        }
    }
}
