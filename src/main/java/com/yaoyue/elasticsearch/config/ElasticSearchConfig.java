package com.yaoyue.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: ElasticSearch配置类
 * @author: WangDongXu (15224931482)
 * @date: 2020/12/30 16:07
 **/
@Configuration
public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("106.14.19.0", 9200, "http")));
        // 需要配置多个节点时，向后依次添加
        return restHighLevelClient;
    }
}
