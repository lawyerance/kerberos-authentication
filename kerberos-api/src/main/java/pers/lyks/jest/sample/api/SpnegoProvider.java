package pers.lyks.jest.sample.api;


import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.*;
import java.io.IOException;
import java.net.URL;

/**
 * @author lawyerance
 * @version 1.0 2019-11-27
 */
public final class SpnegoProvider {
    private static final Logger logger = LoggerFactory.getLogger(SpnegoProvider.class);

    /**
     * Factory for GSS-API mechanism.
     */
    public static final GSSManager GSS_MANAGER = GSSManager.getInstance();

    /**
     * GSS-API mechanism "1.3.6.1.5.5.2".
     */
    public static final Oid SPNEGO_OID = SpnegoProvider.getSpnegoOid();

    /**
     * GSS-API mechanism "1.2.840.113554.1.2.2".
     */
    public static final Oid KERBEROS_V5_OID = SpnegoProvider.getKerberosV5Oid();
    /**
     * Note: The MIT Kerberos V5 mechanism OID is added for compatibility with
     * Chromium-based browsers on POSIX OSes. On these OSes, Chromium erroneously
     * responds to an SPNEGO request with a GSS-API MIT Kerberos V5 mechanism
     * answer (instead of a MIT Kerberos V5 token inside an SPNEGO mechanism answer).
     */
    public static final Oid[] SUPPORTED_OIDS = new Oid[]{SPNEGO_OID, KERBEROS_V5_OID};

    /*
     * This is a utility class (not a Singleton).
     */
    private SpnegoProvider() {
        // default private
    }

    private static final String SPNEGO_MECHANISM = "1.3.6.1.5.5.2";

    /**
     * Returns the Universal Object Identifier representation of
     * the SPNEGO mechanism.
     *
     * @return Object Identifier of the GSS-API mechanism
     */
    private static Oid getSpnegoOid() {
        Oid oid = null;
        try {
            oid = new Oid(SpnegoProvider.SPNEGO_MECHANISM);
        } catch (GSSException gsse) {
            logger.error("Unable to create OID " + SpnegoProvider.SPNEGO_MECHANISM + " !", gsse);
        }
        return oid;
    }


    private static final String KERBEROS_MECHANISM = "1.2.840.113554.1.2.2";

    /**
     * Returns the Universal Object Identifier representation of
     * the MIT Kerberos V5 mechanism.
     *
     * @return Object Identifier of the GSS-API mechanism
     */
    private static Oid getKerberosV5Oid() {
        Oid oid = null;
        try {
            oid = new Oid(SpnegoProvider.KERBEROS_MECHANISM);
        } catch (GSSException gsse) {
            logger.error("Unable to create OID " + SpnegoProvider.KERBEROS_MECHANISM + " !", gsse);
        }
        return oid;
    }

    /**
     * Returns the {@link GSSName} constructed out of the passed-in SPN
     *
     * @param spn The name of Spnego.
     * @return GSSName of URL.
     * @throws GSSException
     */
    public static GSSName createGSSNameForSPN(String spn) throws GSSException {
        return GSS_MANAGER.createName(spn.replaceAll("/", "@"),
            GSSName.NT_HOSTBASED_SERVICE, SpnegoProvider.SPNEGO_OID);
    }

    /**
     * Returns the {@link GSSName} constructed out of the passed-in
     * URL object.
     *
     * @param url HTTP address of server
     * @return GSSName of URL.
     * @throws GSSException
     */
    public static GSSName getServerName(final URL url) throws GSSException {
        return GSS_MANAGER.createName("HTTP@" + url.getHost(),
            GSSName.NT_HOSTBASED_SERVICE, SpnegoProvider.SPNEGO_OID);
    }

    /**
     * Used by the BASIC Auth mechanism for establishing a LoginContext
     * to authenticate a client/caller/request.
     *
     * @param username client username
     * @param password client password
     * @return CallbackHandler to be used for establishing a LoginContext
     */
    public static CallbackHandler getUsernameAndPasswordHandler(final String username, final String password) {

        logger.trace("username=" + username + "; password=" + password.hashCode());

        return new CallbackHandler() {
            @Override
            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

                for (Callback callback : callbacks) {
                    if (callback instanceof NameCallback) {
                        final NameCallback nameCallback = (NameCallback) callback;
                        nameCallback.setName(username);
                    } else if (callback instanceof PasswordCallback) {
                        final PasswordCallback passCallback = (PasswordCallback) callback;
                        passCallback.setPassword(password.toCharArray());
                    } else {
                        logger.warn("Unsupported Callback class=" + callback.getClass().getName());
                    }
                }

            }
        };

    }
}
