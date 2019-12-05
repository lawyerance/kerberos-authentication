package pers.lyks.kerberos.elastic;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import net.minidev.json.JSONArray;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.util.Map;

/**
 * @author lawyerance
 * @version 1.0 2019-11-29
 */
public class CreateIndexAllocatorTest {

    @Test
    public void compatible() {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.should(QueryBuilders.matchQuery("name", "index-001"));

        boolQueryBuilder.should(QueryBuilders.matchQuery("indexCode", "430004403"));
        builder.query(boolQueryBuilder);
        System.out.println(builder);
    }
}
