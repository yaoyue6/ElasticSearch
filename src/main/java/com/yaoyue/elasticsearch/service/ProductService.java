package com.yaoyue.elasticsearch.service;

import com.alibaba.fastjson.JSON;
import com.yaoyue.elasticsearch.entity.Product;
import com.yaoyue.elasticsearch.util.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 解析商品
     * @param keyword
     * @return
     * @throws Exception
     */
    public Boolean parseProduct(String keyword) throws Exception {
        List<Product> list = HtmlParseUtil.parseHtml(keyword);

        BulkRequest request = new BulkRequest("product");

        for (Product product : list) {
            request.add(new IndexRequest("product").source(JSON.toJSONString(product), XContentType.JSON));
        }
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        return !response.hasFailures();
    }

    /**
     * 分页查询
     * @param keyword 关键字
     * @param pageNo 页码
     * @param pageSize 页容量
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> searchRequest(String keyword, int pageNo, int pageSize) throws IOException {
        if (pageNo < 1) {
            pageNo = 1;
        }

        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("product");
        SearchSourceBuilder searchRequestBuilder = new SearchSourceBuilder();

        // 执行分页
        searchRequestBuilder.from((pageNo - 1) * pageSize);
        searchRequestBuilder.size(pageSize);

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<strong style='font-size:24px;color:red'>");
        highlightBuilder.postTags("</strong>");
        searchRequestBuilder.highlighter(highlightBuilder);

        // 精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        searchRequestBuilder.query(termQueryBuilder);
        searchRequestBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 执行搜索
        searchRequest.source(searchRequestBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 解析结果
        List<Map<String, Object>> result = new ArrayList<>();
        for(SearchHit documentFields : search.getHits().getHits()) {
            // 获取高亮字段
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField field = highlightFields.get("title");

            // 原来的结果
            Map<String, Object> source = documentFields.getSourceAsMap();

            // 解析高亮的字段，将原来的字段替换为高亮的字段。
            if (field != null) {
                Text[] fragments = field.fragments();
                StringBuilder stringBuilder = new StringBuilder();
                for (Text text : fragments) {
                    stringBuilder.append(text);
                }
                // 高亮字段替换为原来的字段
                source.put("title", stringBuilder.toString());
            }
            result.add(source);
        }
        return result;
    }
}
