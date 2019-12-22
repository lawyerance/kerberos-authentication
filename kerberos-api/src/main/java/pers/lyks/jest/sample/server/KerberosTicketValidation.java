package pers.lyks.jest.sample.server;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lawyerance
 * @version 1.0 2019-12-01
 */
public class KerberosTicketValidation {

    private final String username;
    private final Subject subject;
    private final byte[] responseToken;
    private final GSSContext gssContext;
    private final GSSCredential delegationCredential;

    public KerberosTicketValidation(String username, String servicePrincipal, byte[] responseToken, GSSContext gssContext) {
        this(username, servicePrincipal, responseToken, gssContext, null);
    }

    public KerberosTicketValidation(String username, String servicePrincipal, byte[] responseToken, GSSContext gssContext, GSSCredential delegationCredential) {
        final Set<KerberosPrincipal> princs = new HashSet<>();
        princs.add(new KerberosPrincipal(servicePrincipal));

        this.username = username;
        this.subject = new Subject(false, princs, new HashSet<>(), new HashSet<>());
        this.responseToken = responseToken;
        this.gssContext = gssContext;
        this.delegationCredential = delegationCredential;
    }

    public KerberosTicketValidation(String username, Subject subject, byte[] responseToken, GSSContext gssContext) {
        this(username, subject, responseToken, gssContext, null);
    }

    public KerberosTicketValidation(String username, Subject subject, byte[] responseToken, GSSContext gssContext, GSSCredential delegationCredential) {
        this.username = username;
        this.subject = subject;
        this.responseToken = responseToken;
        this.gssContext = gssContext;
        this.delegationCredential = delegationCredential;
    }

    public String username() {
        return username;
    }

    public byte[] responseToken() {
        return responseToken;
    }

    public GSSContext getGssContext() {
        return gssContext;
    }

    public Subject subject() {
        return this.subject;
    }

    public GSSCredential getDelegationCredential() {
        return delegationCredential;
    }
}
