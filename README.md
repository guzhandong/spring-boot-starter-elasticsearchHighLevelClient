# spring-boot-starter-elasticsearchHighLevelClient

封装elasticsearch-rest-high-level-client,添加了连接池功能(官方包中没有提供连接池)；同时对接spring-boot-starter

> author : guzhandong  

> email : 569199386@qq.com

> springboot versioni : 2.0.2.RELEASE




## quickstart

> install project for maven
```
git pull git@github.com:guzhandong/spring-boot-starter-elasticsearchHighLevelClient.git
cd spring-boot-starter-elasticsearchHighLevelClient
mvn clean install
```


> add dependent property in pom.xml

```
<dependency>
    <groupId>com.guzhandong.springframework.boot</groupId>
    <artifactId>spring-boot-starter-elasticsearchRestHighLeavelClient</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

```


> add property in application.yml


```
spring:
  es:
    hosts: 192.168.100.1,192.168.100.2

```




> create java file



```



import com.guzhandong.springframework.boot.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class Test {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void test() throws IOException {
        /**
         * 同步方法直接使用
         */
        restHighLevelClient.ping();


        /**
         * 异步方法需要释放client
         */
        restHighLevelClient.getLowLevelClient();
        //异步使用完成后主动释放client
        restHighLevelClient.releaseClient();


        /**
         * 执行多条异步操作，最后释放client 即可
         */
        restHighLevelClient.getLowLevelClient();
        restHighLevelClient.getLowLevelClient();
        restHighLevelClient.getLowLevelClient();
        //最后调用释放即可
        //因为在异步操作的时候，如果发现当前线程有持有的client 会先释放再重新从资源池中获取一个client 进行调用
        restHighLevelClient.releaseClient();
    }
}

```



## all configuration
```
spring:
  es:
    hosts: 192.168.100.1,192.168.100.2
  pool:

```

## todo list

1. 修复JMX在springboot中和eureka集成导致注册失败，当前默认设置为关闭连接池的JMX来保证集成可用。