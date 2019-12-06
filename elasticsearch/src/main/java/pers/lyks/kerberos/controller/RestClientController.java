package pers.lyks.kerberos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lawyerance
 * @version 1.0 2019-11-22
 */
@RestController
@RequestMapping(value = "/rest", produces = "application/json; charset=utf-8")
public class RestClientController {
    private static final Logger logger = LoggerFactory.getLogger(RestClientController.class);
    @Resource
    private RestClient restClient;

    @Resource
    private ObjectMapper objectMapper;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Map<String, Object> information() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        Response response = restClient.performRequest(new Request("GET", "/"));
        map.put("data", objectMapper.readTree(response.getEntity().getContent()));
        return map;
    }

    @RequestMapping(value = "/_cat/indices", method = RequestMethod.GET)
    public String list() throws IOException {
        Response response = restClient.performRequest(new Request("GET", "/_cat/indices"));
        return EntityUtils.toString(response.getEntity());
    }

    @RequestMapping(value = {"/{index}", "/{index}/{type}"}, method = RequestMethod.GET)
    public Map<String, Object> index(@PathVariable String index, @PathVariable(required = false) String type) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        StringBuffer buffer = new StringBuffer("/");
        buffer.append(index).append("/");
        if (StringUtils.hasText(type)) {
            buffer.append(type).append("/");
        }
        buffer.append("_search");
        Request request = new Request("POST", buffer.toString());
        Response response = restClient.performRequest(request);

        map.put("data", objectMapper.readTree(response.getEntity().getContent()));
        return map;
    }

    @RequestMapping(value = "/{index}", method = RequestMethod.PUT)
    public Map<String, Object> createIndex(@PathVariable String index, @RequestBody String body) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        Request request = new Request("PUT", "/" + index);
        logger.info("Create index mapping: {}", body);
        Response response = restClient.performRequest(request);
        map.put("data", objectMapper.readTree(response.getEntity().getContent()));
        return map;
    }

    @RequestMapping(value = "/{index}", method = RequestMethod.DELETE)
    public Map<String, Object> delete(@PathVariable String index) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        Response response = restClient.performRequest(new Request("DELETE", "/" + index));
        map.put("data", objectMapper.readTree(response.getEntity().getContent()));
        return map;
    }

    @RequestMapping(value = "/{index}/{type}/{id}", method = RequestMethod.POST)
    public Map<String, Object> insert(@PathVariable String index, @PathVariable String type, @PathVariable String id, @RequestBody String body) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "OK");
        Request request = new Request("POST", "/" + index + "/" + type + "/" + id);
        request.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        builder.addHeader("Content-Type", "application/json; charset=utf-8");
        request.setOptions(builder);
        logger.info("Insert index data with: {}", body);
        Response response = restClient.performRequest(request);
        map.put("data", objectMapper.readTree(response.getEntity().getContent()));
        return map;
    }
}
