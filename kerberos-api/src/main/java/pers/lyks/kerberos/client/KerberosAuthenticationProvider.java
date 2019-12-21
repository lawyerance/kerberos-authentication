package pers.lyks.kerberos.client;

import pers.lyks.kerberos.api.KerberosClient;

/**
 * @author lawyerance
 * @version 1.0 2019-12-01
 */
public class KerberosAuthenticationProvider {

    private KerberosClient kerberosClient;

    public KerberosAuthenticationProvider(KerberosClient kerberosClient) {
        this.kerberosClient = kerberosClient;
    }
}
