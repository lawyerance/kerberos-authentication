package pers.lyks.jest.sample.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lawyerance
 * @version 1.0 2019-12-07
 */
public class Krb5Property {
    private boolean debug = false;
    private boolean storeKey = false;
    private boolean doNotPrompt = false;
    private boolean useTicketCache = false;
    private boolean useKeyTab = false;
    private String ticketCacheName = null;
    private String keyTabName = null;
    private String principal = null;

    private boolean useFirstPass = false;
    private boolean tryFirstPass = false;
    private boolean storePass = false;
    private boolean clearPass = false;
    private boolean refresh = false;
    private boolean renew = false;
    // specify if initiator.
    // perform authentication exchange if initiator
    private boolean initiator = true;

    public Map<String, String> buildOptions() {
        Map<String, String> opts = new HashMap<>();
        opts.put("debug", String.valueOf(this.debug));
        opts.put("doNotPrompt", String.valueOf(this.doNotPrompt));
        opts.put("storeKey", String.valueOf(this.storeKey));
        opts.put("useTicketCache=", String.valueOf(this.useTicketCache));
        opts.put("useKeyTab", String.valueOf(this.useKeyTab));
        opts.put("ticketCache", this.ticketCacheName);
        opts.put("keyTab", this.keyTabName);
        opts.put("principal", this.principal);
        opts.put("refreshKrb5Config", String.valueOf(this.refresh));
        opts.put("renewTGT", String.valueOf(this.renew));
        opts.put("isInitiator", String.valueOf(this.initiator));
        opts.put("tryFirstPass", String.valueOf(this.tryFirstPass));
        opts.put("useFirstPass", String.valueOf(this.useFirstPass));
        opts.put("storePass", String.valueOf(this.storePass));
        opts.put("clearPass", String.valueOf(this.clearPass));

        return Collections.unmodifiableMap(opts);
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isStoreKey() {
        return storeKey;
    }

    public boolean isDoNotPrompt() {
        return doNotPrompt;
    }

    public boolean isUseTicketCache() {
        return useTicketCache;
    }

    public boolean isUseKeyTab() {
        return useKeyTab;
    }

    public String getTicketCacheName() {
        return ticketCacheName;
    }

    public String getKeyTabName() {
        return keyTabName;
    }

    public String getPrincipal() {
        return principal;
    }

    public boolean isUseFirstPass() {
        return useFirstPass;
    }

    public boolean isTryFirstPass() {
        return tryFirstPass;
    }

    public boolean isStorePass() {
        return storePass;
    }

    public boolean isClearPass() {
        return clearPass;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public boolean isRenew() {
        return renew;
    }

    public boolean isInitiator() {
        return initiator;
    }
}
