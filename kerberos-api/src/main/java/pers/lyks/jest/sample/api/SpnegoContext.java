package pers.lyks.jest.sample.api;

import org.ietf.jgss.*;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * @author lawyerance
 * @version 1.0 2019-11-27
 */
public final class SpnegoContext {
    private final LoginContext context;

    public SpnegoContext(LoginContext context) {
        this.context = context;
    }

    public GSSContext getContext(String nameStr) throws GSSException, PrivilegedActionException {
        GSSName gssName = SpnegoProvider.GSS_MANAGER.createName(nameStr, GSSName.NT_HOSTBASED_SERVICE, SpnegoProvider.SPNEGO_OID);
        return Subject.doAs(context.getSubject(), (PrivilegedExceptionAction<GSSContext>) () -> {
            GSSCredential credential = SpnegoProvider.GSS_MANAGER.createCredential(null
                , GSSCredential.DEFAULT_LIFETIME
                , SpnegoProvider.SUPPORTED_OIDS
                , GSSCredential.INITIATE_ONLY);
            GSSContext context = SpnegoProvider.GSS_MANAGER.createContext(gssName
                , SpnegoProvider.SPNEGO_OID
                , credential
                , GSSContext.DEFAULT_LIFETIME);

            context.requestMutualAuth(true);
            context.requestConf(true);
            context.requestInteg(true);
            context.requestReplayDet(true);
            context.requestSequenceDet(true);
            return context;
        });
    }

    public byte[] createAuthenticateHeader(String nameStr) throws PrivilegedActionException, GSSException {
        GSSContext ctx = getContext(nameStr);
        return Subject.doAs(context.getSubject(), (PrivilegedExceptionAction<byte[]>) () -> ctx.initSecContext(new byte[0], 0, 0));
    }


    private static void runAuthentication(JaasSubjectContainer container, String username, int lifetimeInSeconds, String targetService) {
        try {
            GSSManager manager = GSSManager.getInstance();
            GSSName clientName = manager.createName(username, GSSName.NT_USER_NAME);

            GSSCredential clientCredential = manager.createCredential(
                clientName,
                lifetimeInSeconds,
                SpnegoProvider.SUPPORTED_OIDS,
                GSSCredential.INITIATE_ONLY
            );

            GSSName serverName = manager.createName(targetService, GSSName.NT_USER_NAME);

            GSSContext securityContext = manager.createContext(serverName,
                SpnegoProvider.KERBEROS_V5_OID,
                clientCredential,
                GSSContext.DEFAULT_LIFETIME);

            securityContext.requestCredDeleg(true);
            securityContext.requestInteg(false);
            securityContext.requestAnonymity(false);
            securityContext.requestMutualAuth(false);
            securityContext.requestReplayDet(false);
            securityContext.requestSequenceDet(false);

            boolean established = false;

            byte[] outToken = new byte[0];

            while (!established) {
                byte[] inToken = new byte[0];
                outToken = securityContext.initSecContext(inToken, 0, inToken.length);

                established = securityContext.isEstablished();
            }

            container.addToken(targetService, outToken);
        } catch (Exception e) {
            throw new RuntimeException("Kerberos authentication failed", e);
        }
    }
}
