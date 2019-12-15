package pers.lyks.kerberos.elastic.compatible;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;

/**
 * @author lawyerance
 * @version 1.0 2019-11-28
 */
public class CreateIndexIncludeTypeAllocator extends ResetURIAllocator {
    private static final ObjectMapper mapper = new ObjectMapper();

    public CreateIndexIncludeTypeAllocator(URIBuilder uri) {
        super(uri);
    }

    @Override
    protected void customize(URIBuilder uriBuilder) {
        uriBuilder.addParameter("include_type_name", "true");
    }

    @Override
    protected boolean rewrite(HttpRequest httpRequest) {
        return super.rewrite(httpRequest);
    }
}
