package pers.lyks.kerberos.controller;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lawyerance
 * @version 1.0 2019-11-22
 */
@RestController
@RequestMapping(value = "/rest-high-level", produces = "application/json; charset=utf-8")
public class RestHighClientController {
    private static final Logger logger = LoggerFactory.getLogger(RestHighClientController.class);
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Map<String, Object> information() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        MainResponse info = restHighLevelClient.info(RequestOptions.DEFAULT);
        XContentBuilder xContentBuilder = info.toXContent(JsonXContent.contentBuilder(), new ToXContent.MapParams(new HashMap<>()));
        BytesReference bytesReference = BytesReference.bytes(xContentBuilder);
        Map<String, Object> toMap = XContentHelper.convertToMap(xContentBuilder.contentType().xContent(), bytesReference.streamInput(), true);
        map.put("data", toMap);
        return map;
    }

    @RequestMapping(value = {"/{index}", "/{index}/{type}"}, method = RequestMethod.GET)
    public Map<String, Object> index(@PathVariable String index, @PathVariable(required = false) String type) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        SearchRequest searchRequest = new SearchRequest().indices(index).source(new SearchSourceBuilder().query(QueryBuilders.boolQuery()));
        if (type != null) {
            searchRequest.types(type);
        }
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.printf("Search result count - %s. \n", hits.totalHits);
        map.put("data", Stream.of(hits.getHits()).map(SearchHit::getSourceAsMap).collect(Collectors.toList()));
        return map;
    }

    @RequestMapping(value = "/{index}", method = RequestMethod.PUT)
    public Map<String, Object> createIndex(@PathVariable String index, @RequestBody String body) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        CreateIndexRequest indexRequest = new CreateIndexRequest(index);
        logger.info("Create index mapping: {}", body);
        indexRequest.source(body, XContentType.JSON);
        CreateIndexResponse response = restHighLevelClient.indices().create(indexRequest, RequestOptions.DEFAULT);
        map.put("data", response);
        return map;
    }

    @RequestMapping(value = "/{index}", method = RequestMethod.DELETE)
    public Map<String, Object> delete(@PathVariable String index) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        AcknowledgedResponse response = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        map.put("data", response);
        return map;
    }

    @RequestMapping(value = "/{index}/{type}/{id}", method = RequestMethod.POST)
    public Map<String, Object> insert(@PathVariable String index, @PathVariable String type, @PathVariable String id, @RequestBody String body) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        IndexRequest indexRequest = new IndexRequest(index, type).id(id);
        logger.info("Insert index data with: {}", body);
        indexRequest.source(body, XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        map.put("data", response);
        return map;
    }
}
