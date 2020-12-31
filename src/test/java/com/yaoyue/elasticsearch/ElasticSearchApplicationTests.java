package com.yaoyue.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.yaoyue.elasticsearch.entity.User;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ElasticSearchApplicationTests {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     * @throws IOException
     */
    @Test
    public void testCreateIndex() throws IOException {
        // 1、创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("student");

        // 2、客户端执行创建请求，请求后获得响应
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

        System.out.println(response);
    }

    /**
     * 获取索引
     * @throws IOException
     */
    @Test
    public void testExistIndex() throws IOException {
        // 1、测试获取索引
        GetIndexRequest request = new GetIndexRequest("student");

        // 判断是否存在索引
        boolean flag = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(flag);

        // 测试获取索引
        GetIndexResponse response = restHighLevelClient.indices().get(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 删除索引
     * @throws IOException
     */
    @Test
    public void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("student");

        AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 添加文档
     * @throws IOException
     */
    @Test
    public void testAddDocument() throws IOException {
        // 创建对象
        User user = new User(1L, "张三", 20, "男", Lists.newArrayList("打游戏", "运动", "看书"));

        // 创建请求
        IndexRequest request = new IndexRequest("student");
        // 设置请求规则
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(100));

        // 将数据加入到请求中
        IndexRequest source = request.source(JSON.toJSONString(user), XContentType.JSON);

        // 客户端发送请求，获取响应的结果
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        System.out.println(index.toString());
        System.out.println(index.status());
    }

    /**
     * 判断文档是否存在
     * @throws IOException
     */
    public void testExistDocument() throws IOException {
        GetRequest request = new GetRequest("student", "1");

        // 是否获取返回数据_source的上下文
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");

        boolean flag = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        System.out.println(flag);
    }

    /**
     * 获取文档内容
     * @throws IOException
     */
    @Test
    public void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("student", "1");
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        System.out.println("id: " + response.getId());
        System.out.println("source: " + response.getSource());
        System.out.println("index: " + response.getIndex());
        System.out.println("fields: " + response.getFields());
        System.out.println("name: " + response.getField("name"));
        System.out.println("version: " + response.getVersion());
        System.out.println("primaryTerm: " + response.getPrimaryTerm());
        System.out.println("seqNo: " + response.getSeqNo());
        System.out.println("sourceInternal: " + response.getSourceInternal());
    }

    /**
     * 更新文档内容
     * @throws IOException
     */
    @Test
    public void testUpdateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("student", "1");
        request.timeout("1s");

        User user = new User(1L, "李四", 28, "女", Lists.newArrayList("学习", "运动", "看书"));
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    /**
     * 删除指定id的文档
     * @throws IOException
     */
    @Test
    public void testDeleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("student", "1");

        request.timeout("1s");
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);

        System.out.println(response.status());
    }

    @Test
    public void testBatchAddDocument() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        List<User> list = new ArrayList<>();
        list.add(new User(1L, "张三", 20, "男", Lists.newArrayList("打游戏", "运动", "看书")));
        list.add(new User(2L, "李四", 21, "女", Lists.newArrayList("打游戏", "看书")));
        list.add(new User(3L, "王五", 22, "男", Lists.newArrayList("Rap", "桌游", "看书")));
        list.add(new User(4L, "沈佳宜", 23, "女", Lists.newArrayList("跳舞", "唱歌", "看书")));
        list.add(new User(5L, "紫霞", 24, "女", Lists.newArrayList("打篮球", "运动", "打羽毛球")));
        list.add(new User(6L, "至尊宝", 25, "男", Lists.newArrayList("看书", "打麻将", "台球")));

        for (User user : list) {
            bulkRequest.add(new IndexRequest("student").id(user.getId() + "").source(JSON.toJSONString(user),  XContentType.JSON));
        }
        BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        // 判断是否执行成功
        System.out.println(response.hasFailures());
    }
    
    @Test
    public void testSearchDocument() throws IOException {
        SearchRequest request = new SearchRequest("student");
        
        // 使用建造者模式构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 搜索结果高亮显示
        // HighlightBuilder highlightBuilder = new HighlightBuilder();
        // searchSourceBuilder.highlighter();

        // QueryBuilders：使用此工具类来构建查询条件

        // 精确搜索
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("age", 22);
        searchSourceBuilder.query(termQueryBuilder);

        // 模糊搜索
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();

        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 构建分页，存在默认值，也可手动填充
        searchSourceBuilder.from();
        searchSourceBuilder.size();
        request.source(searchSourceBuilder);

        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(response.getHits()));

        for(SearchHit documentFields : response.getHits().getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }
    }
}
