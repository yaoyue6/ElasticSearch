package com.yaoyue.elasticsearch.controller;

import com.alibaba.fastjson.JSON;
import com.yaoyue.elasticsearch.entity.Product;
import com.yaoyue.elasticsearch.util.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * @description:
 * @author: WangDongXu (15224931482)
 * @date: 2020/12/31 14:16
 **/
public class ProductController {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    public void createIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("product");
        restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
    }

    public Boolean parseProduct(String keyword) throws Exception {
        List<Product> list = HtmlParseUtil.parseHtml(keyword);

        BulkRequest request = new BulkRequest("product");

        for (Product product : list) {
            request.add(new IndexRequest("product").source(JSON.toJSONString(product), XContentType.JSON));
        }
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        return response.hasFailures();
    }
}
