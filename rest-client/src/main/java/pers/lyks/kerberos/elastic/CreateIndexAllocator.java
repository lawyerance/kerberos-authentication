package pers.lyks.kerberos.elastic;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author lawyerance
 * @version 1.0 2019-11-28
 */
public class CreateIndexAllocator implements CompatibleAllocator {
    private static final Configuration DEFAULT_CONF = Configuration.defaultConfiguration();
    private static final JsonPath MAPPING_PATH = JsonPath.compile("$.mappings");
    private static final JsonPath MAPPING_NEXT_PATH = JsonPath.compile("$.mappings.*");

    @Override
    public void compatible(HttpRequest request, HttpContext context) {
        HttpPut unwrap = unwrap(request);
        if (unwrap != null) {
            try {
                InputStream content = unwrap.getEntity().getContent();
                DocumentContext parse = JsonPath.parse(content, DEFAULT_CONF);

                JSONArray array = parse.read(MAPPING_NEXT_PATH);
                parse.delete(MAPPING_NEXT_PATH);
                for (Object item : array) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) item;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        parse = parse.put(MAPPING_PATH, entry.getKey(), entry.getValue());
                    }
                }
                unwrap.setEntity(new StringEntity(parse.jsonString()));
                request = HttpRequestWrapper.wrap(request);
            } catch (IOException e) {
                //Ignore
            }
        }

    }

    private HttpPut unwrap(HttpRequest request) {
        if (request instanceof HttpRequestWrapper) {
            HttpRequest original = ((HttpRequestWrapper) request).getOriginal();
            if (original instanceof HttpPut) {
                return (HttpPut) original;
            }
        } else if (request instanceof HttpPut) {
            return (HttpPut) request;
        }
        return null;
    }
}
