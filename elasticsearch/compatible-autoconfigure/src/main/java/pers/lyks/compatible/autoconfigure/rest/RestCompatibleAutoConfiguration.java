package pers.lyks.compatible.autoconfigure.rest;

import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import pers.lyks.elasticsearch.CompatibleClient6to7Interceptor;
import pers.lyks.elasticsearch.server.MainVersion;

import java.io.IOException;

/**
 * @author lawyerance
 * @version 1.0 2019-11-22
 */
@Configuration
@ConditionalOnClass(value = {RestClient.class})
public class RestCompatibleAutoConfiguration {


    @Configuration
    @ConditionalOnClass(value = {RestClientBuilder.class, RestHighLevelClient.class})
    public static class CompatibleRestClientBuilderCustomizer implements RestClientBuilderCustomizer {
        private static final Logger logger = LoggerFactory.getLogger(CompatibleRestClientBuilderCustomizer.class);

        public CompatibleRestClientBuilderCustomizer() {
        }

        @Override
        public void customize(RestClientBuilder builder) {
            boolean compatible;
            RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);
            try {
                MainResponse info = restHighLevelClient.info(RequestOptions.DEFAULT);
                compatible = MainVersion.compatible(info);
            } catch (IOException e) {
                logger.error("Obtain elasticsearch sever version error", e);
                compatible = true;
            }
            if (compatible) {
                builder.setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.addInterceptorFirst(new CompatibleClient6to7Interceptor());
                    return httpClientBuilder;
                });
            }
        }
    }

}
