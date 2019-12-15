package pers.lyks.kerberos.elastic.compatible;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.lyks.kerberos.elastic.CompatibleAllocator;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implement a compatible abstraction by modifying the request uri.
 *
 * @author lawyerance
 * @version 1.0 2019-11-28
 */
abstract class ResetURIAllocator implements CompatibleAllocator {
    protected static final String DEFAULT_TYPE_NAME = "_doc";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private URIBuilder builder;

    protected ResetURIAllocator(URIBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void compatible(HttpRequest request, HttpContext context) {
        if (rewrite(request)) {
            //
            URI renew;
            try {
                customize(builder);
                renew = this.builder.build();
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
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("The request uri needs not to be write with request line: {}", request.getRequestLine());
        }
    }

    protected boolean rewrite(HttpRequest httpRequest) {
        return true;
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
