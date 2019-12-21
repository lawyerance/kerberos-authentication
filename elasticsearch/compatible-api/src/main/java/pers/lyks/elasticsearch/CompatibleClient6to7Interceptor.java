package pers.lyks.elasticsearch;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.protocol.HttpContext;
import pers.lyks.elasticsearch.compatible.CreateIndexAllocator;
import pers.lyks.elasticsearch.compatible.PutDataAllocator;
import pers.lyks.elasticsearch.compatible.SearchAllocator;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * <p>Compatible interceptor that using elasticsearch client 6 connect elasticsearch server 7,both jest client and rest client. </p>
 *
 * @author lawyerance
 * @version 1.0 2019-11-28
 */
public class CompatibleClient6to7Interceptor implements HttpRequestInterceptor {

    @Override
    public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
        RequestLine requestLine = httpRequest.getRequestLine();
        try {
            URIBuilder builder = new URIBuilder(requestLine.getUri());
            distributor(builder, requestLine.getMethod()).compatible(httpRequest, httpContext);
        } catch (URISyntaxException e) {
            throw new HttpException("Parse request uri error with request line: " + requestLine, e);
        }
    }

    private CompatibleAllocator distributor(URIBuilder builder, String method) {
        String path = builder.getPath();
        if (HttpGet.METHOD_NAME.equalsIgnoreCase(method)) {
            if (path.endsWith("/_search")) {
                return new SearchAllocator(builder);
            }
        } else if (HttpPost.METHOD_NAME.equalsIgnoreCase(method)) {
            if (path.endsWith("/_search")) {
                return new SearchAllocator(builder);
            }
            // put data
        } else if (HttpPut.METHOD_NAME.equalsIgnoreCase(method)) {
            //create index or put data
            PathLayering p = new PathLayering(path);
            int size = p.getSize();
            if (size == 1) {
                return new CreateIndexAllocator(builder);
            } else {
                return new PutDataAllocator(p, builder);
            }
        }
        return (request, context) -> {
        };
    }

}
