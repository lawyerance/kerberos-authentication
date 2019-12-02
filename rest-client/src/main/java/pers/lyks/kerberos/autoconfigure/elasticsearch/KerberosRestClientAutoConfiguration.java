package pers.lyks.kerberos.autoconfigure.elasticsearch;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.SecureString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.security.PrivilegedActionException;

/**
 * @author lawyerance
 * @version 1.0 2019-11-22
 */
@ConditionalOnClass(value = {RestClient.class})
public class KerberosRestClientAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(KerberosRestClientAutoConfiguration.class);

    @Configuration
    @ConditionalOnClass(value = {RestClientBuilder.class, RestHighLevelClient.class})
    @EnableConfigurationProperties({KerberosRestClientProperties.class})
    @ConditionalOnProperty(name = "spring.elasticsearch.rest.kerberos.enabled", matchIfMissing = true)
    public static class KerberosAuthenticateRestClientBuilderCustomizer implements RestClientBuilderCustomizer {
        private final KerberosRestClientProperties.KerberosProperties kerberos;

        public KerberosAuthenticateRestClientBuilderCustomizer(KerberosRestClientProperties kerberosRestClientProperties) {
            this.kerberos = kerberosRestClientProperties.getKerberos();
        }

        @Override
        public void customize(RestClientBuilder builder) {
            logger.info("Using custom customizer to authenticate rest client of kerberos.");
            SecureString secureString = StringUtils.isEmpty(kerberos.getPassword()) ? null : new SecureString(kerberos.getPassword().toCharArray());
            KerberosAuthenticHttpClientConfigCallbackHandler configCallbackHandler = new KerberosAuthenticHttpClientConfigCallbackHandler(false, kerberos.getUsername(), secureString, kerberos.getLoginModule());
            try {
                configCallbackHandler.login();
            } catch (PrivilegedActionException e) {
                logger.error("Kerberos authenticate error with username [ {} ] ", kerberos.getUsername(), e);
            }
            builder.setHttpClientConfigCallback(configCallbackHandler);
        }
    }

    @Configuration
    @ConditionalOnClass(value = {RestClientBuilder.class, RestHighLevelClient.class})
    @EnableConfigurationProperties({KerberosRestClientProperties.class})
    public static class CompatibleRestClientBuilderCustomizer implements RestClientBuilderCustomizer {
        private final boolean compatible;

        public CompatibleRestClientBuilderCustomizer(KerberosRestClientProperties kerberosRestClientProperties) {
            this.compatible = kerberosRestClientProperties.isCompatible();
        }

        @Override
        public void customize(RestClientBuilder builder) {
            if (compatible) {
                builder.setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.addInterceptorFirst(new CompatibleRestClient6to7Interceptor());
                    return httpClientBuilder;
                });
            }
        }
    }

}
