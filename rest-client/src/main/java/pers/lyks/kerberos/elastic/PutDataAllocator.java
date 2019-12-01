package pers.lyks.kerberos.elastic;

import org.apache.http.client.utils.URIBuilder;

/**
 * @author lawyerance
 * @version 1.0 2019-11-28
 */
public class PutDataAllocator extends ResetRequestURIAllocator {

    private PathLayering path;

    public PutDataAllocator(PathLayering path, URIBuilder builder) {
        super(builder);
        this.path = path;
    }

    @Override
    protected void customize(URIBuilder builder) {
        path.update(1, "_doc");
        builder.setPath(path.toString());
    }
}
