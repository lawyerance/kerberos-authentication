package pers.lyks.kerberos.elastic;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implement a compatible abstraction by modifying the request uri.
 *
 * @author lawyerance
 * @version 1.0 2019-11-28
 */
abstract class ResetRequestURIAllocator implements CompatibleAllocator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private URIBuilder uriBuilder;

    protected ResetRequestURIAllocator(URIBuilder builder) {
        this.uriBuilder = builder;
    }

    @Override
    public void compatible(HttpRequest request, HttpContext context) {

        URI renew;
        try {
            customize(uriBuilder);
            renew = this.uriBuilder.build();
        } catch (URISyntaxException e) {
            //Ignore
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Before handle request with request line: {}", request.getRequestLine());
            rewrite(renew, request, context);
            logger.debug("After rewrite request uri with request line: {}", request.getRequestLine());
        } else {
            rewrite(renew, request, context);
        }
    }

    protected void customize(URIBuilder builder) {
    }

    private void rewrite(URI renew, HttpRequest request, HttpContext context) {
        if (request instanceof HttpRequestWrapper) {
            ((HttpRequestWrapper) request).setURI(renew);
        } else {
            // maybe not happened
            HttpCoreContext adapt = HttpCoreContext.adapt(context);
            request = HttpRequestWrapper.wrap(request, adapt.getTargetHost());
        }
    }
}
