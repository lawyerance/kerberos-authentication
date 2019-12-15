package pers.lyks.kerberos.elastic;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

/**
 * Use elasticsearch rest client 6 compatible elasticsearch server 7 compatible implementation interface
 * <p>All compatible methods are implemented by modifying the uri.</p>
 *
 * @author lawyerance
 * @version 1.0 2019-11-28
 */
@FunctionalInterface
public interface CompatibleAllocator {

    /**
     * Handle compatible implementations by modifying rest request.
     *
     * @param request The rest client http request object.
     * @param context The rest client http context object
     */
    void compatible(HttpRequest request, HttpContext context);
}
