package com.guzhandong.springframework.boot.elasticsearch.pool;

import com.guzhandong.springframework.boot.elasticsearch.client.ElasticsearchClientConfigure;
import com.guzhandong.springframework.boot.elasticsearch.utils.LogUtil;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ElasticsearchClientFactory implements PooledObjectFactory<RestHighLevelClient>{

    private LogUtil logUtil = LogUtil.getLogger(getClass());

    private ElasticsearchClientConfigure elasticsearchClientConfigure;

    public ElasticsearchClientFactory(ElasticsearchClientConfigure elasticsearchClientConfigure) {
        this.elasticsearchClientConfigure = elasticsearchClientConfigure;
    }

    /**
     * 异步httpclient的连接超时配置
     * @param builder
     */
    private void setConnectTimeOutConfig(RestClientBuilder builder) {
        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                if (elasticsearchClientConfigure.getConnectTimeOut()>0) {
                    requestConfigBuilder.setConnectTimeout(elasticsearchClientConfigure.getConnectTimeOut());
                }
                if (elasticsearchClientConfigure.getSocketTimeOut()>0) {
                    requestConfigBuilder.setSocketTimeout(elasticsearchClientConfigure.getSocketTimeOut());
                }
                if (elasticsearchClientConfigure.getConnectionRequestTimeOut()>0) {
                    requestConfigBuilder.setConnectionRequestTimeout(elasticsearchClientConfigure.getConnectionRequestTimeOut());
                }
                return requestConfigBuilder;
            }
        });
    }

    /**
     * 异步httpclient的连接数配置
     * @param builder
     */
    private void setMutiConnectConfig(RestClientBuilder builder) {
        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                if (elasticsearchClientConfigure.getMaxConnectNum()>0) {
                    httpClientBuilder.setMaxConnTotal(elasticsearchClientConfigure.getMaxConnectNum());
                }
                if (elasticsearchClientConfigure.getMaxConnectPerRoute()>0) {
                    httpClientBuilder.setMaxConnPerRoute(elasticsearchClientConfigure.getMaxConnectPerRoute());
                }
                return httpClientBuilder;
            }
        });
    }

//    @Override
//    public PooledObject<RestHighLevelClient> makeObject() throws Exception {
//        Set<String > hostSet = new HashSet<String>(this.elasticsearchClientConfigure.getHosts().length+this.elasticsearchClientConfigure.getHosts().length/3);
//        for (String h:this.elasticsearchClientConfigure.getHosts()){
//            hostSet.add(h);
//        }
//        HttpHost[] httpHosts = hostSet.stream()
//                .map(host->new HttpHost(host, elasticsearchClientConfigure.getPort(), elasticsearchClientConfigure.getSchema()))
//                .toArray(len->new HttpHost[len]);
//        RestClientBuilder clientBuilder = RestClient.builder(httpHosts);
//        RestHighLevelClient client = new RestHighLevelClient(clientBuilder);
//        return new DefaultPooledObject(client);
//
//    }
    @Override
    public PooledObject<RestHighLevelClient> makeObject() throws Exception {
        Set<String > hostSet = new HashSet<String>(this.elasticsearchClientConfigure.getHosts().length+this.elasticsearchClientConfigure.getHosts().length/3);
        for (String h:this.elasticsearchClientConfigure.getHosts()){
            hostSet.add(h);
        }
        HttpHost[] httpHosts = hostSet.stream()
                .map(host->new HttpHost(host.split("\\:")[0], Integer.valueOf(host.split("\\:")[1]), elasticsearchClientConfigure.getSchema()))
                .toArray(len->new HttpHost[len]);
        RestClientBuilder clientBuilder = RestClient.builder(httpHosts);
        RestHighLevelClient client = new RestHighLevelClient(clientBuilder);
        return new DefaultPooledObject(client);

    }

    @Override
    public void destroyObject(PooledObject<RestHighLevelClient> p) throws Exception {
        if (p.getObject()!=null) {
            try {
                if (p.getObject().ping()) {
                    p.getObject().close();
                }
            } catch (IOException e) {
                logUtil.debug("es http client close exception:{}",e.getMessage());
            }
        }

    }

    @Override
    public boolean validateObject(PooledObject<RestHighLevelClient> p) {
        try {
            if (p.getObject()!=null && p.getObject().ping()) {
                return true;
            }
        } catch (IOException e) {
            logUtil.debug("es http client ping exception:{}",e.getMessage());
        }
        return false;
    }

    @Override
    public void activateObject(PooledObject<RestHighLevelClient> p) throws Exception {
        boolean result =false;
        try {
            result = p.getObject().ping();
        } catch (IOException e) {
            logUtil.debug("http pool active client ,ping result :{}",result);
        }

    }

    @Override
    public void passivateObject(PooledObject<RestHighLevelClient> p) throws Exception {
        //nothing
    }
}
