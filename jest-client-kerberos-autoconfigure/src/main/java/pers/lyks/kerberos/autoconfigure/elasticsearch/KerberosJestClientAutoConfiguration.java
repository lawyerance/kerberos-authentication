package pers.lyks.kerberos.autoconfigure.elasticsearch;

import com.google.gson.Gson;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.jest.HttpClientConfigBuilderCustomizer;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.lyks.elastic.CompatibleClient6to7Interceptor;

import java.security.PrivilegedActionException;

/**
 * @author lawyerance
 * @version 1.0 2019-12-01
 */
@Configuration
@ConditionalOnClass(value = {JestClient.class})
public class KerberosJestClientAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(KerberosJestClientAutoConfiguration.class);

    @Configuration
    @EnableConfigurationProperties({KerberosJestClientProperties.class})
    @ConditionalOnProperty(name = "spring.elasticsearch.jest.kerberos.enabled", matchIfMissing = true)
    public static class KerberosHttpClientConfigBuilderCustomizer implements HttpClientConfigBuilderCustomizer {

        private final KerberosProperties kerberos;

        public KerberosHttpClientConfigBuilderCustomizer(KerberosJestClientProperties kerberosJestClientProperties) {
            this.kerberos = kerberosJestClientProperties.getKerberos();
        }

        @Override
        public void customize(HttpClientConfig.Builder builder) {
            logger.info("Using custom customizer to authenticate rest client of kerberos.");
            HttpClientConfigHandler clientConfigHandler = new HttpClientConfigHandler(kerberos.getUsername(), kerberos.getPassword(), kerberos.getLoginModule());
            try {
                builder.credentialsProvider(clientConfigHandler.credentialsProvider());
            } catch (PrivilegedActionException e) {
                logger.error("create credentials provider error", e);
            }
        }
    }


    @Configuration
    @EnableConfigurationProperties({KerberosJestClientProperties.class})
    public static class Compatible extends JestAutoConfiguration {
        private boolean compatible;

        public Compatible(JestProperties properties, ObjectProvider<Gson> gson, ObjectProvider<HttpClientConfigBuilderCustomizer> builderCustomizers, KerberosJestClientProperties kerberosJestClientProperties) {
            super(properties, gson, builderCustomizers);
            this.compatible = kerberosJestClientProperties.isCompatible();
        }

        @Bean(destroyMethod = "shutdownClient")
        @ConditionalOnProperty(name = "spring.elasticsearch.jest.kerberos.enabled", havingValue = "false")
        public JestClient jestClient() {
            JestClientFactory factory = compatible ? new CompatibleJestClientFactory() : new JestClientFactory();
            factory.setHttpClientConfig(createHttpClientConfig());
            return factory.getObject();
        }
    }


    public static class CompatibleJestClientFactory extends JestClientFactory {
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
