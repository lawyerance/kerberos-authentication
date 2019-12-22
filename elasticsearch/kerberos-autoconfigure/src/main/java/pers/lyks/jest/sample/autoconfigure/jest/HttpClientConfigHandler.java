package pers.lyks.jest.sample.autoconfigure.jest;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.KerberosCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;
import pers.lyks.jest.sample.autoconfigure.BaseHttpClientConfig;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * @author lawyerance
 * @version 1.0 2019-12-07
 */
public final class HttpClientConfigHandler extends BaseHttpClientConfig {

    public HttpClientConfigHandler(String userPrincipalName, String password, String loginModule) {
        super(userPrincipalName, password, loginModule);
    }

    public CredentialsProvider credentialsProvider() throws PrivilegedActionException {
        GSSManager gssManager = GSSManager.getInstance();
        LoginContext loginContext = super.login();
        GSSCredential credential = Subject.doAs(loginContext.getSubject(), (PrivilegedExceptionAction<GSSCredential>) () -> gssManager.createCredential(null, GSSCredential.DEFAULT_LIFETIME, SPNEGO_OIS, GSSCredential.INITIATE_ONLY));
        KerberosCredentialsProvider credentialsProvider = new KerberosCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthSchemes.SPNEGO),
            new KerberosCredentials(credential));
        return credentialsProvider;
    }

}
