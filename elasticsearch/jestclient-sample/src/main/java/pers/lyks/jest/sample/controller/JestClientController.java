package pers.lyks.jest.sample.controller;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.cluster.NodesInfo;
import io.searchbox.core.*;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lawyerance
 * @version 1.0 2019-11-22
 */
@RestController
@RequestMapping(value = "/jest", produces = "application/json; charset=utf-8")
public class JestClientController {
    private static final Logger logger = LoggerFactory.getLogger(JestClientController.class);
    @Resource
    private JestClient jestClient;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Map<String, Object> information() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        NodesInfo.Builder builder = new NodesInfo.Builder();
        JestResult execute = jestClient.execute(builder.build());
        map.put("data", execute.getJsonObject());
        return map;
    }

    @RequestMapping(value = "/_cat/indices", method = RequestMethod.GET)
    public String list() throws IOException {
        Cat.IndicesBuilder builder = new Cat.IndicesBuilder();
        CatResult result = jestClient.execute(builder.build());
        return result.getJsonString();
    }

    @RequestMapping(value = {"/{index}", "/{index}/{type}"}, method = RequestMethod.GET)
    public Map<String, Object> index(@PathVariable String index, @PathVariable(required = false) String type) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        Search.Builder search = new Search.Builder("{}");
        search.addIndex(index);
        if (StringUtils.hasText(type)) {
            search.addType(type);
        }
        SearchResult response = jestClient.execute(search.build());

        map.put("data", response.getJsonObject());
        return map;
    }

    @RequestMapping(value = "/{index}", method = RequestMethod.PUT)
    public Map<String, Object> createIndex(@PathVariable String index, @RequestBody String body) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        CreateIndex.Builder createBuilder = new CreateIndex.Builder(index);
        createBuilder.mappings(body);
        logger.info("Create index mapping: {}", body);
        JestResult result = jestClient.execute(createBuilder.build());
        map.put("data", result.getJsonObject());
        return map;
    }

    @RequestMapping(value = "/{index}", method = RequestMethod.DELETE)
    public Map<String, Object> delete(@PathVariable String index) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        JestResult result = jestClient.execute(new DeleteIndex.Builder(index).build());
        map.put("data", result);
        return map;
    }

    @RequestMapping(value = "/{index}/{type}/{id}", method = RequestMethod.POST)
    public Map<String, Object> insert(@PathVariable String index, @PathVariable String type, @PathVariable String id, @RequestBody String body) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        Index.Builder indexBuilder = new Index.Builder(body);
        indexBuilder.id(id).index(index).type(type);

        logger.info("Insert index data with: {}", body);
        DocumentResult result = jestClient.execute(indexBuilder.build());
        map.put("data", result.getJsonObject());
        return map;
    }
}
