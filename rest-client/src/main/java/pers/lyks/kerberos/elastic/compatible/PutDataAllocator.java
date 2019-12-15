package pers.lyks.kerberos.elastic.compatible;

import org.apache.http.client.utils.URIBuilder;
import pers.lyks.kerberos.elastic.PathLayering;

/**
 * @author lawyerance
 * @version 1.0 2019-11-28
 */
public class PutDataAllocator extends ResetURIAllocator {

    private PathLayering path;

    public PutDataAllocator(PathLayering path, URIBuilder builder) {
        super(builder);
        this.path = path;
    }

    @Override
    protected void customize(URIBuilder builder) {
        super.customize(builder);
        path.update(1, "_doc");
        builder.setPath(path.toString());
    }
}
