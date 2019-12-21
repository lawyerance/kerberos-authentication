package pers.lyks.elastic.compatible;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;

/**
 * <p>Creating an index using compatibility mode, supports both elasticsearch 6 and 7 mappings format.</p>
 * <p>Judge whether compatible with mapping format，if compatible mode that add parameter 'include_type_name=true' to url query string.</p>
 *
 * @author lawyerance
 * @version 1.0 2019-11-28
 */
public class CreateIndexAllocator extends ResetURIAllocator {
    private static final ObjectMapper mapper = new ObjectMapper();

    public CreateIndexAllocator(URIBuilder uri) {
        super(uri);
    }

    @Override
    protected void customize(URIBuilder builder) {
        builder.addParameter("include_type_name", "true");
    }

    @Override
    protected boolean rewrite(HttpRequest httpRequest) {
        if (httpRequest instanceof HttpRequestWrapper) {
            HttpPut original = (HttpPut) ((HttpRequestWrapper) httpRequest).getOriginal();
            try {
                JsonNode readTree = mapper.readTree(original.getEntity().getContent());
                JsonNode properties = readTree.at("/mappings/properties");
                //If exist json path 'mappings.properties', the mapping does not include type
                return properties instanceof MissingNode;
            } catch (IOException e) {
                // Ignore
            }
        }
        return super.rewrite(httpRequest);
    }
}
