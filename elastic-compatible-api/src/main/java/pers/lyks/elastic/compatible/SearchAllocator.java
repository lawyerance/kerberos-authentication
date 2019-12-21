package pers.lyks.elastic.compatible;

import org.apache.http.client.utils.URIBuilder;
import pers.lyks.elastic.PathLayering;

import java.util.Objects;

/**
 * <p>Search results, if search uri contains type name replacing custom type to default type name {@link ResetURIAllocator#DEFAULT_TYPE_NAME}. </p>
 * <p>Transforming rest total hits compatible: add a parameter 'rest_total_hits_as_int=true' to url query string.</p>
 *
 * @author lawyerance
 * @version 1.0 2019-11-28
 */
public class SearchAllocator extends ResetURIAllocator {
    private PathLayering layering;

    public SearchAllocator(URIBuilder build) {
        this(build.getPath(), build);
    }

    public SearchAllocator(String path, URIBuilder build) {
        this(new PathLayering(Objects.requireNonNull(path, "The request path must not be null.")), build);
    }

    private SearchAllocator(PathLayering layering, URIBuilder build) {
        super(build);
        this.layering = layering;
    }

    @Override
    protected void customize(URIBuilder builder) {
        super.customize(builder);
        if (layering.getSize() >= 3) {
            layering.delete(1);
            builder.setPath(layering.toString());
        }
        builder.addParameter("rest_total_hits_as_int", "true");
    }
}
