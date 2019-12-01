package pers.lyks.kerberos.elastic;

import org.apache.http.client.utils.URIBuilder;

/**
 * @author lawyerance
 * @version 1.0 2019-11-28
 */
public class CreateIndexIncludeTypeAllocator extends ResetRequestURIAllocator {


    public CreateIndexIncludeTypeAllocator(URIBuilder uri) {
        super(uri);
    }

    @Override
    protected void customize(URIBuilder uriBuilder) {
        uriBuilder.addParameter("include_type_name", "true");
    }
}
