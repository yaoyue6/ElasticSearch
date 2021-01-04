# ElasticSearch

Lucene是一套信息检索工具包，不包含搜索引擎系统。（Jar包）

包含内容：索引结构、读写索引的工具、搜索规则、工具类等。

Lucene 和 ElasticSearch 的关系：

Elastic Search 是基于 Lucene做了一些封装和增强。

## ElasticSearch简介

​	ElasticSearch，简称es，es是一个开源的高扩展的分布式全文检索引擎，它可以近乎实时的存储、检索数据；本身扩展性很好，可以扩展到上百台服务器，处理PB级别（大数据时代）的数据。es也使用Java开发并使用Lucene作为其核心来实现所有索引和搜索的功能。但是它的目的是通过简单的Restful API来隐藏Lucene的复杂性，从而让全文搜索变得简单。

​	ElasticSearch 用于全文搜索、结构化搜索、分析以及三者混合使用。

## ES和Solr的区别

### Solr简介

​	Solr是Apache下的一个顶级开源项目，采用Java开发，是基于Lucene的全文搜索服务器。Solr提供了比Lucene更为丰富的查询语言，同时实现了可配置、可扩展，并对索引、搜索性能进行了优化。

​	Solr可以独立运行在Jetty、Tomcat等这些Servlet容器中，实现方法是使用POST方法向Solr服务器发送一个描述Field及其内容的XML文档，Solr根据XMl文档添加、删除、更新索引。

​	Solr搜索只需要发送HTTP GET请求，然后对，然后对Solr返回Xml、JSON等格式的查询结果进行解析，组织页面布局。

​	Solr对外提供类似于Web-service的API接口。

### ElasticSearch vs Solr总结

1、es 基本是开箱即用，无需安装。

2、Solr 使用 zookeeper 进行分布式管理，而 ElasticSearch 自身带有分布式协调管理系统。

3、Solr 支持更多格式的数据，比如XML、JSON、CSV等，而 ElasticSearch 仅支持 JSON 文件格式。

4、Solr 官方提供的功能多，而ElasticSearch本身更注重于核心功能，高级功能由第三方插件提供。

5、Solr 查询尽快，但更新索引满（插入删除慢）

- ES 建立索引快（即查询慢），实时性查询快
- Solr 是传统搜索应用的有力解决方案，但ElasticSearch更适用于新兴的实时搜索应用。



## ElasticSearch目录

```javascript
bin		启动文件
config	配置文件
	log4j2.properties	日志配置文件
    elasticsearch.yml	elasticsearch的配置文件
modules	功能模块
logs	日志模块
plugins	插件
lib		相关jar包
```

需要修改的配置：

```
配置跨域
http.cors.enabled: true
http.cors.allow-origin: "*"

设置最大和最小容量
-Xmx128m
-Xms128m
```



## ES核心概念

ElasticSearch是面向文档的，由JSON构成

| Relational DB      | ElasticSearch   |
| ------------------ | --------------- |
| 数据库（DataBase） | 索引（Indices） |
| 表（tables）       | Types           |
| 行（rows）         | Documents       |
| 字段（columns）    | Fields          |

ElasticSearch集群中可以包含多个索引（数据库），每个索引中包含多个类型（表），每个类型下包含多个文档（行），每个文档中包含多个字段（列）。

### **物理设计**

elasticsearch在后台把每个索引划分成多个分片，每片分片可以在集群中的不同服务器间迁移。直接启动就是以集群方式启动，默认的集群名是elasticsearch。

### **逻辑设计：**

一个索引类型中，包含多个文档，如文档、文档2，当我们索引一篇文档时，可以用过索引 -> 类型 -> 文档ID来查询具体的文档。

### 文档

​	elasticsearch是面向文档的，因此索引和搜索数据的最小单位就是文档。文档的属性：

- 自我包含，一篇文档同时包含字段和对应的值，即同时包含key和value
- 可以是层次型的，一个文档中包含自文档，复杂的逻辑实体就是这么来的。
- 灵活的结构，文档不依赖预先定义的模式，可以忽略字段，也可以动态添加字段。

### 类型

​	类型是文档的逻辑容器，类似表，类型中对于字段的定义称为映射，如name映射为字符串类型 。elasticsearch新增字段的操作：elasticsearch自动将新字段加入映射中，但是该字段不确定类型，elasticsearch推测类型，但不一定推测正确，因此最安全的方法就是提前定义好所需要的映射。

### 索引

​	索引是映射类型的容器，存储了映射类型的字段和其它设置。

### 节点和分片的工作方式

​	一个集群至少有1个节点，而一个节点就是一个elasticsearch进程，节点可以是多个索引默认的，默认是5个分片（又称主分片）构成，每个主分片会有一个副本（又称复制分片）。

​	主分片和复制分片不会在同一个节点上，有利于某个节点挂了，不会造成数据丢失。实质：一个分片就是一个Lucene索引，一个包含倒排索引的文件目录，倒排索引的结构使得elasticsearch在不扫面全部文档的情况下，就能告诉你哪些文档包含特定的关键字。

​	**elasticsearch检索快的核心：倒排索引。**

### 倒排索引

​	elasticsearch采用倒排索引的结构，采用Lucene倒排索引作为底层，适合于快速的全文检索。

​	创建倒排索引之前，会将每个文档拆分成独立的词（词条或tokens），然后创建一个包含所有不重复的词条的排序列表，列出每个词条出现在哪个文档。（相当于创建一个倒排索引表，对词出现的文档进行记录，检索时直接分词，之后去查询词对应的文档，减少了检索无关数据的时间消耗）



## 默认分词器

1、Standard：标准分词，默认以单词进行切分，并转化为小写；

2、Simple：按照非单词进行切分，并转化为小写；

3、WhiteSpace：按照空格进行切分；

4、Stop：去除Stop Word语气助词，如a、an、the等；

5、Keyword：传入关键词，不做分词处理。

```
GET _analyze
{
	"analyze":"keyword"
	"text":"it is a rainy day!"
}
```



## IK分词器

​	分词：即把一段中文或者字符划分为一个个的关键字，在搜索时会把自己的信息进行分词，会把数据库或索引库中的数据进行分词，然后进行匹配操作。elasticsearch默认的分词器是将每一个字看成一个词，比如：“倒排索引”被切分为“倒”、“排”、“索”、“引”。

​	IK分词器提供了两个分词算法：ik_smart和ik_max_word，其中ik_smart是最少切分，ik_max_word是最细粒度划分。

​	需要自己配置分词可以自己在../ik/config/中定义Xxx.dic，记录分词，之后加载到 **IKAnalyzer.cfg.xml** 中，实质是 **<entry key="ext_dict">Xxx.dic</entry>** ，最后重启./elasticsearch

```
GET _analyze
{
	"analyze":"ik_max_word"
	"text":"今天天气真好！"
}
```



## REST风格说明

​	主要用于客户端和服务器端交互，基于此风格设计的软件可以更简洁，更有层次，更易于实现缓存等机制。

基于REST命令说明：

| Method |                   URL地址                    |          描述          |
| :----: | :------------------------------------------: | :--------------------: |
|  PUT   |     IP地址:9200/索引名称/类型名称/文档id     | 创建文档（指定文档id） |
|  POST  |        IP地址:9200/索引名称/类型名称         | 创建文档（随机文档id） |
|  POST  | IP地址:9200/索引名称/类型名称/文档id/_update |        修改文档        |
| DELETE |     IP地址:9200/索引名称/类型名称/文档id     |        删除文档        |
|  GET   |     IP地址:9200/索引名称/类型名称/文档id     | 通过文档id查询指定文档 |
|  POST  |    IP地址:9200/索引名称/类型名称/_search     |      查询所有文档      |



## 索引的基本操作

1、创建一个索引

```
PUT /索引名/类型名/文档id
PUT /movie/movie1/1
{
  "id":1,
  "name":"红海行动",
  "score":9.8
}

PS: PUT /student/_doc/1		此时_doc为默认类型，从elasticsearch7之后开始废弃类型，而采用_doc，也可不写
```

2、数据类型

- 字符串类型

  text、keyword

- 数值类型

  long、integer、short、byte、double、float、half、scaled float

- 日期类型

  date

- 布尔类型

  boolean

- 二进制类型

  binary

- 等等...

### text 和 keyword的区别

​	text：索引长文本，如**电子邮件、描述**等。在建立索引之前会被分词器进行分词，转换为词组。es可以对分词之后的词组进行**检索**，但text类型**不能用于过滤、排序和聚合**等操作。

​	keyword：常用来形容**电子邮箱地址、主机名、状态码**等，**不进行分词**，常常用来**过滤、排序和聚合**等。

3、指定字段的类型，创建具体的索引规则

```
PUT /book
{
  "mappings": {
    "properties":{
      "name":{
        "type":"text"
      },
      "age":{
        "type":"integer"
      },
      "id":{
        "type": "long"
      }
    }
  }
}
```

4、获得索引的信息

```
GET /book
```

5、如果文档中的字段没有指明类型，那么elasticsearch会默认配置字段类型

6、查看默认配置

```
查看健康状况：GET _cat/health
查看版本状况：GET _cat/indices?v
```

7、更新文档

- 直接使用PUT再次提交，即会覆盖原数据
- 使用 POST /movie/movie1/2/_update 命令修改原纪录

```
POST /movie/movie1/2/_update
{
  "doc":{
    "name":"冰雪奇缘"
  }
}
```

8、删除 DELETE

```
删除文档：DELETE /movie/movie1/3
删除索引：DELETE /student
```

根据请求url来判断删除索引还是删除文档



## 文档的基本操作

### 1、添加文档数据

```
PUT /movie/movie1/1
{
  "id":1,
  "name":"红海行动",
  "score":9.8
}
```

### 2、查询数据

```
GET /movie/movie/1
```

### 3、更新数据

```
POST /movie/movie1/2/_update
{
  "doc":{
    "name":"冰雪奇缘"
  }
}
```

### 4、条件查询

```
GET /movie/movie1/_search?q=name:红海
```



## 复杂搜索

### 1、条件查询    

​	使用JSON构建查询的参数体

```
GET /movie/movie1/_search
{
  "query":{
    "match": {
      "name": "冰雪"
    }
  }
}
```

过滤结果，只显示若干字段，相当于 select column1, column2...

```
GET /movie/movie1/_search
{
  "query":{
    "match": {
      "name": "冰雪"
    }
  },
  "_source":["name", "id"]
}
```

### 2、排序

```
GET /movie/movie1/_search
{
  "query":{
    "match": {
      "name": "冰雪"
    }
  },
  "sort":[
    {
      "score":{ 
        "order":"asc"
      }    
    }
  ]
}
```

**添加排序规则，可以在sort中的 {} 后追加**

### 3、分页查询

```
GET /movie/movie1/_search
{
  "query":{
    "match": {
      "name": "冰雪"
    }
  },
  "from":0,   // 从第几条数据开始
  "size":2	  // 返回的数据条数（单页面的数据）
}
```

### 4、精确匹配

and		类似于where column1 = xxx and column2 = xxx

```
GET /movie/movie1/_search
{
  "query":{
    "bool":{
      "must":[
        {
          "match":{
            "name":"肖申克"
          }
        },
        {
          "match":{
            "score":"9.8"
          }
        }
      ]
    }
  }
}
```

ps：**不能将两个查询条件放在一个match中**，否则会报错，报错原因如下：

```
[match] query doesn't support multiple fields, found [name] and [score]
```

or 	类似于where column1 = xxx or column2 = xxx

```
GET /movie/movie1/_search
{
  "query":{
    "bool":{
      "should":[
        {
          "match":{
            "name":"肖申克"
          }
        },
        {
          "match":{
            "score":"9.9"
          }
        }
      ]
    }
  }
}
```

not  	类似于where column1 != xxx and column2 != xxx

```
GET /movie/movie1/_search
{
  "query":{
    "bool":{
      "must_not":[
        {
          "match":{
            "name":"肖申克"
          }
        },
        {
          "match":{
            "score":"9.9"
          }
        }
      ]
    }
  }
}
```

### 5、过滤

```
GET /movie/movie1/_search
{
  "query":{
    "bool":{
      "filter":{
        "range":{
          "id":{
            "lt": 6,
            "gt": 2
          }
        }
      }
    }
  }
}
```

gt	   大于	

lt		小于

gte	 大于等于

lte	  小于等于

### 6、匹配多条件进行查询

```
GET /movie/movie1/_search
{
  "query":{
    "match": {
      "name":"肖申克 世界 冰雪"
    }
  }
}
```

使用 空格 隔开，也可使用bool + should实现

### 7、精确查询

term查询是直接通过倒排索引指定的词条进行精确查找的，即直接对关键词进行查找，常用于精确查找

match在匹配时会对所查找的关键词进行分词，然后按分词匹配查找，常用于模糊查找

```
GET /movie/movie1/_search
{
  "query":{
    "bool":{
      "must":[
        {
          "term":{
            "name":"肖申克的救赎"
          }
        }
      ]
    }
  }
}
```

### 8、高亮显示

```
GET /movie/movie1/_search
{
  "query":{
    "match":{
      "name":"肖申克"
    }
  },
  "highlight":{
    "fields":{
      "name":{}
    }
  }
}
```

自定义高亮

```
GET /movie/movie1/_search
{
  "query":{
    "match":{
      "name":"肖申克"
    }
  },
  "highlight":{
    "pre_tags": "<p style='color:red;font-size:40px'>", 
    "fields":{
      "name":{}
    },
    "post_tags": "</p>"
  }
}
```



## 通过Java操作索引/文档

测试代码

```java
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
```

注册组件

```java
@Bean
public RestHighLevelClient restHighLevelClient() {
    RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
        RestClient.builder(new HttpHost("106.14.19.0", 9200, "http")));
    // 需要配置多个节点时，向后依次添加
    return restHighLevelClient;
}
```

导入依赖

```xml
<!-- Model TO JSON -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.75</version>
</dependency>

<!-- ElasticSearch jar -->
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>7.9.3</version>
</dependency>

<!-- lombok plugin -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<!-- jSoup解析网页 -->
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.10.2</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```



