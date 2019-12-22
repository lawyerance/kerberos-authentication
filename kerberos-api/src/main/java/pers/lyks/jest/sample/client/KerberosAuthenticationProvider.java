package pers.lyks.jest.sample.client;

import pers.lyks.jest.sample.api.KerberosClient;

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
