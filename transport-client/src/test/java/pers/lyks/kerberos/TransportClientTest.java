package pers.lyks.kerberos;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author lawyerance
 * @version 1.0 2019-11-25
 */
public class TransportClientTest {

    @Test
    public void testSearch() throws IOException {
        Settings.Builder builder = Settings.builder()
                .put("client.transport.ignore_cluster_name", true);
//                .put("cluster.name", "es672");
        TransportClient client = new PreBuiltTransportClient(builder.build())
                .addTransportAddress(new TransportAddress(new InetSocketAddress("localhost", 9300)));

        SearchRequest searchRequest = new SearchRequest().indices("my_index").source(new SearchSourceBuilder().query(QueryBuilders.boolQuery()));
        SearchResponse response = client.search(searchRequest).actionGet();
        long count = 1;
        for (SearchHit hit : response.getHits()) {
            System.out.printf("Result index [ %s ], with response body: %s \n", count++, hit.getSourceAsMap());
        }
    }
}