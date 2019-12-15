package pers.lyks.kerberos.autoconfigure.elasticsearch;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.KerberosCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.common.settings.SecureString;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashSet;

/**
 * @version 1.0 2019-12-07
 * @auther lawyerance
 */
public final class HttpClientConfigHandler {
    private static final Oid SPNEGO_OIS = getSpnegoOid();

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
    private String password;
    private final String loginModule;

    public HttpClientConfigHandler(String userPrincipalName, String password, String loginModule) {
        this.userPrincipalName = userPrincipalName;
        this.password = password;
        this.loginModule = loginModule;
    }

    public CredentialsProvider credentialsProvider() throws PrivilegedActionException {
        GSSManager gssManager = GSSManager.getInstance();
        LoginContext loginContext = this.login();
        GSSCredential credential = Subject.doAs(loginContext.getSubject(), (PrivilegedExceptionAction<GSSCredential>) () -> gssManager.createCredential(null, GSSCredential.DEFAULT_LIFETIME, SPNEGO_OIS, GSSCredential.INITIATE_ONLY));
        KerberosCredentialsProvider credentialsProvider = new KerberosCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthSchemes.SPNEGO),
            new KerberosCredentials(credential));
        return credentialsProvider;
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
        private final String password;

        KebCallbackHandler(String principal, String password) {
            this.principal = principal;
            this.password = password;
        }

        @Override
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback callback : callbacks) {
                if (callback instanceof PasswordCallback) {
                    PasswordCallback pc = (PasswordCallback) callback;
                    if (pc.getPrompt().contains(principal)) {
                        pc.setPassword(password.toCharArray());
                        break;
                    }
                }
            }
        }
    }
}
