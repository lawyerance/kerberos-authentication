package pers.lyks.elasticsearch.server;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.Version;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author lawyerance
 * @version 1.0 2019-12-15
 */
public final class MainVersion {
    public static final Logger logger = LoggerFactory.getLogger(MainVersion.class);

    public static boolean compatible(HttpHost host, HttpAsyncClientBuilder httpClientBuilder) throws IOException, ExecutionException, InterruptedException {
        CloseableHttpAsyncClient httpAsyncClient = httpClientBuilder.build();
        httpAsyncClient.start();
        Future<HttpResponse> execute = httpAsyncClient.execute(new HttpGet(host.toURI()), null);
        HttpEntity entity = execute.get().getEntity();
        return MainVersion.compatible(entity.getContent());
    }

    public static boolean asyncCompatible(HttpHost host, HttpAsyncClientBuilder httpClientBuilder) throws IOException, ExecutionException, InterruptedException {
        CloseableHttpAsyncClient httpAsyncClient = httpClientBuilder.build();
        httpAsyncClient.start();
        Future<HttpResponse> execute = httpAsyncClient.execute(new HttpGet(host.toURI()), null);
        HttpEntity entity = execute.get().getEntity();
        return MainVersion.compatible(entity.getContent());
    }

    public static boolean compatible(MainResponse main) throws IOException {
        Version serverVersion = main.getVersion();
        logger.info("Elasticsearch use client version {} connect sever version {}. ", Version.CURRENT, serverVersion);
        return serverVersion.major > Version.CURRENT.major;
    }


    private static boolean compatible(InputStream is) throws IOException {
        JsonXContent jsonXContent = JsonXContent.jsonXContent;
        XContentParser parser = jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, is);
        MainResponse response = MainResponse.fromXContent(parser);
        return compatible(response);
    }
}
