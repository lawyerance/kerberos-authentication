package pers.lyks.elastic.compatible;

import org.apache.http.client.utils.URIBuilder;
import pers.lyks.elastic.PathLayering;

/**
 * <p>Add a single record, replace custom type to default type name {@link ResetURIAllocator#DEFAULT_TYPE_NAME}. </p>
 *
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
        path.update(1, DEFAULT_TYPE_NAME);
        builder.setPath(path.toString());
    }
}
