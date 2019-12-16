package pers.lyks.kerberos.autoconfigure.elasticsearch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.KerberosCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.common.settings.SecureString;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;
import pers.lyks.kerberos.elastic.MainInfoVersion;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;
import java.io.IOException;
import java.security.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author lawyerance
 * @version 1.0 2019-11-23
 */
class KerberosAuthenticHttpClientConfigCallbackHandler implements RestClientBuilder.HttpClientConfigCallback {
    private static final Oid SPNEGO_OIS = getSpnegoOid();

    public KerberosAuthenticHttpClientConfigCallbackHandler(HttpHost host, String userPrincipalName, SecureString password, String loginModule) {
        this.userPrincipalName = userPrincipalName;
        this.password = password;
        this.loginModule = loginModule;
        this.host = host;
    }

    private static Oid getSpnegoOid() {
        Oid oid;
        try {
            oid = new Oid("1.3.6.1.5.5.2");
        } catch (GSSException gsse) {
            throw ExceptionsHelper.convertToRuntime(gsse);
        }
        return oid;
    }

    private final String userPrincipalName;
    private final SecureString password;
    private final String loginModule;
    private HttpHost host;

    @Override
    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
        Registry<AuthSchemeProvider> authSchemaRegistry = RegistryBuilder.<AuthSchemeProvider>create().register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory()).build();
        GSSManager gssManager = GSSManager.getInstance();
        GSSCredential credential;
        try {
            LoginContext loginContext = login();
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
            CloseableHttpAsyncClient httpAsyncClient = httpClientBuilder.build();
            httpAsyncClient.start();
            Future<HttpResponse> execute = httpAsyncClient.execute(host, new HttpGet("/"), null);
            HttpEntity entity = execute.get().getEntity();
            compatible = MainInfoVersion.compatible(entity.getContent());
        } catch (InterruptedException | ExecutionException | IOException e) {
            // Ignore
            compatible = true;
        }
        if (compatible) {
            httpClientBuilder.addInterceptorFirst(new CompatibleRestClient6to7Interceptor());
        }
        return httpClientBuilder;
    }

    LoginContext login() throws PrivilegedActionException {
        return AccessController.doPrivileged((PrivilegedExceptionAction<LoginContext>) () -> {
            final Subject subject = new Subject(false, Collections.singleton(new KerberosPrincipal(userPrincipalName)), new HashSet<>(), new HashSet<>());
            final CallbackHandler callback;
            if (password != null) {
                callback = new KebCallbackHandler(userPrincipalName, password);
            } else {
                callback = null;
            }
            final LoginContext loginContext = new LoginContext(this.loginModule, subject, callback, null);
            loginContext.login();
            return loginContext;
        });
    }

    /**
     * This class matchs {@link AuthScope} and based on that returns
     * {@link Credentials}. Only supports {@link AuthSchemes#SPNEGO} in
     * {@link AuthScope#getScheme()}
     */
    static class KerberosCredentialsProvider implements CredentialsProvider {
        private AuthScope authScope;
        private Credentials credentials;

        @Override
        public void setCredentials(AuthScope authscope, Credentials credentials) {
            if (authscope.getScheme().regionMatches(true, 0, AuthSchemes.SPNEGO, 0, AuthSchemes.SPNEGO.length())) {
                this.authScope = authscope;
                this.credentials = credentials;
                return;
            }
            throw new IllegalArgumentException("Only " + AuthSchemes.SPNEGO + " auth schema is supported in AuthScope.");
        }

        @Override
        public Credentials getCredentials(AuthScope authscope) {
            assert this.authScope != null && authscope != null;
            return authscope.match(this.authScope) > -1 ? this.credentials : null;
        }

        @Override
        public void clear() {
            this.authScope = null;
            this.credentials = null;
        }
    }

    /**
     * JAAS call back handle to provide credentials
     */
    static class KebCallbackHandler implements CallbackHandler {
        private final String principal;
        private final SecureString password;

        KebCallbackHandler(String principal, SecureString password) {
            this.principal = principal;
            this.password = password;
        }

        @Override
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback callback : callbacks) {
                if (callback instanceof PasswordCallback) {
                    PasswordCallback pc = (PasswordCallback) callback;
                    if (pc.getPrompt().contains(principal)) {
                        pc.setPassword(password.getChars());
                        break;
                    }
                }
            }
        }
    }
}
