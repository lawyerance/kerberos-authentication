package pers.lyks.kerberos.autoconfigure.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.SecureString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientBuilderCustomizer;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import pers.lyks.elastic.CompatibleClient6to7Interceptor;
import pers.lyks.elastic.util.MainInfoVersion;

import java.io.IOException;
import java.security.PrivilegedActionException;

/**
 * @author lawyerance
 * @version 1.0 2019-11-22
 */
@Configuration
@ConditionalOnClass(value = {RestClient.class})
public class KerberosRestClientAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(KerberosRestClientAutoConfiguration.class);

    @Configuration
    @ConditionalOnClass(value = {RestClientBuilder.class, RestHighLevelClient.class})
    @EnableConfigurationProperties({KerberosRestClientProperties.class})
    @ConditionalOnProperty(name = "spring.elasticsearch.rest.kerberos.enabled", matchIfMissing = true)
    public static class KerberosAuthenticateRestClientBuilderCustomizer implements RestClientBuilderCustomizer {
        private final KerberosRestClientProperties.KerberosProperties kerberos;
        private final RestClientProperties restClientProperties;

        public KerberosAuthenticateRestClientBuilderCustomizer(KerberosRestClientProperties kerberosRestClientProperties, RestClientProperties restClientProperties) {
            this.kerberos = kerberosRestClientProperties.getKerberos();
            this.restClientProperties = restClientProperties;
        }

        @Override
        public void customize(RestClientBuilder builder) {
            logger.info("Using custom customizer to authenticate rest client of kerberos.");
            HttpHost[] hosts = restClientProperties.getUris().stream().map(HttpHost::create).toArray(HttpHost[]::new);
            SecureString secureString = StringUtils.isEmpty(kerberos.getPassword()) ? null : new SecureString(kerberos.getPassword().toCharArray());
            KerberosAuthenticHttpClientConfigCallbackHandler configCallbackHandler = new KerberosAuthenticHttpClientConfigCallbackHandler(hosts[0], kerberos.getUsername(), secureString, kerberos.getLoginModule());
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
    @ConditionalOnProperty(name = "spring.elasticsearch.rest.kerberos.enabled", havingValue = "false")
    public static class CompatibleRestClientBuilderCustomizer implements RestClientBuilderCustomizer {

        public CompatibleRestClientBuilderCustomizer() {
        }

        @Override
        public void customize(RestClientBuilder builder) {
            boolean compatible;
            RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);
            try {
                MainResponse info = restHighLevelClient.info(RequestOptions.DEFAULT);
                compatible = MainInfoVersion.compatible(info);
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
