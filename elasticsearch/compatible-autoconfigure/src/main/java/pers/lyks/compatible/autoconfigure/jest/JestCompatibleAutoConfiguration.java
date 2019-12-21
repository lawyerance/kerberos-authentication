package pers.lyks.compatible.autoconfigure.jest;

import com.google.gson.Gson;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.elasticsearch.jest.HttpClientConfigBuilderCustomizer;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.lyks.elasticsearch.CompatibleClient6to7Interceptor;

/**
 * @author lawyerance
 * @version 1.0 2019-12-01
 */
@Configuration
@ConditionalOnClass(value = {JestClient.class})
public class JestCompatibleAutoConfiguration {

    @Configuration
    @EnableConfigurationProperties({JestProperties.class})
    public static class Compatible extends JestAutoConfiguration {

        public Compatible(JestProperties properties, ObjectProvider<Gson> gson, ObjectProvider<HttpClientConfigBuilderCustomizer> builderCustomizers) {
            super(properties, gson, builderCustomizers);
        }

        @Bean(destroyMethod = "shutdownClient")
        public JestClient jestClient() {
            JestClientFactory factory = new CompatibleJestClientFactory();
            factory.setHttpClientConfig(createHttpClientConfig());
            return factory.getObject();
        }
    }


    static class CompatibleJestClientFactory extends JestClientFactory {
        @Override
        protected HttpClientBuilder configureHttpClient(HttpClientBuilder builder) {
            builder.addInterceptorFirst(new CompatibleClient6to7Interceptor());
            return super.configureHttpClient(builder);
        }

        @Override
        protected HttpAsyncClientBuilder configureHttpClient(HttpAsyncClientBuilder builder) {
            builder.addInterceptorFirst(new CompatibleClient6to7Interceptor());
            return super.configureHttpClient(builder);
        }
    }
}
