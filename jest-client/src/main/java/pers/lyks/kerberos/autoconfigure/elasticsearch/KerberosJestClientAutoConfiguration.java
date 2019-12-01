package pers.lyks.kerberos.autoconfigure.elasticsearch;

import io.searchbox.client.JestClient;
import io.searchbox.client.config.HttpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.jest.HttpClientConfigBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lawyerance
 * @version 1.0 2019-12-01
 */
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
        }
    }
}
