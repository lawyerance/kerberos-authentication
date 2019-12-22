package pers.lyks.jest.sample.autoconfigure.rest;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.KerberosCredentials;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.client.RestClientBuilder;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;
import pers.lyks.elasticsearch.CompatibleClient6to7Interceptor;
import pers.lyks.elasticsearch.server.MainVersion;
import pers.lyks.jest.sample.autoconfigure.BaseHttpClientConfig;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.io.IOException;
import java.security.*;
import java.util.concurrent.ExecutionException;

/**
 * @author lawyerance
 * @version 1.0 2019-11-23
 */
class KerberosAuthenticHttpClientConfigCallbackHandler extends BaseHttpClientConfig implements RestClientBuilder.HttpClientConfigCallback {

    public KerberosAuthenticHttpClientConfigCallbackHandler(HttpHost host, String userPrincipalName, String password, String loginModule) {
        super(userPrincipalName, password, loginModule);
        this.host = host;
    }


    private HttpHost host;

    @Override
    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
        Registry<AuthSchemeProvider> authSchemaRegistry = RegistryBuilder.<AuthSchemeProvider>create().register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory()).build();
        GSSManager gssManager = GSSManager.getInstance();
        GSSCredential credential;
        try {
            LoginContext loginContext = super.login();
            credential = Subject.doAs(loginContext.getSubject(), (PrivilegedExceptionAction<GSSCredential>) () -> gssManager.createCredential(null, GSSCredential.DEFAULT_LIFETIME, SPNEGO_OIS, GSSCredential.INITIATE_ONLY));
            KerberosCredentialsProvider credentialsProvider = new KerberosCredentialsProvider();
            credentialsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthSchemes.SPNEGO),
                new KerberosCredentials(credential)
            );
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        } catch (PrivilegedActionException e) {
            throw ExceptionsHelper.convertToRuntime(e);
        }
        httpClientBuilder.setDefaultAuthSchemeRegistry(authSchemaRegistry);
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        try {
            sslContextBuilder.loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true);
            httpClientBuilder.setSSLContext(sslContextBuilder.build());
            httpClientBuilder.setSSLStrategy(new SSLIOSessionStrategy(sslContextBuilder.build(), new NoopHostnameVerifier()));
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
        boolean compatible;
        try {
            compatible = MainVersion.asyncCompatible(host, httpClientBuilder);
        } catch (InterruptedException | ExecutionException | IOException e) {
            // Ignore
            compatible = true;
        }
        if (compatible) {
            httpClientBuilder.addInterceptorFirst(new CompatibleClient6to7Interceptor());
        }
        return httpClientBuilder;
    }


}
