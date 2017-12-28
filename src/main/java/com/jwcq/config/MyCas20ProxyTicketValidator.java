package com.jwcq.config;

/**
 * Created by luotuo on 17-6-21.
 */
import java.util.Arrays;
import java.util.List;
import org.jasig.cas.client.util.XmlUtils;
import org.jasig.cas.client.validation.*;

public class MyCas20ProxyTicketValidator extends MyCas20ServiceTicketValidator {
    private boolean acceptAnyProxy;
    private ProxyList allowedProxyChains = new ProxyList();
    private boolean allowEmptyProxyChain = true;

    public MyCas20ProxyTicketValidator(String casServerUrlPrefix) {
        super(casServerUrlPrefix);
    }

    protected final ProxyList getAllowedProxyChains() {
        return this.allowedProxyChains;
    }

    protected String getUrlSuffix() {
        return "proxyValidate";
    }

    protected void customParseResponse(String response, Assertion assertion) throws TicketValidationException {
        System.out.println("dfdddddddddddddddddddddddddddddddd");
        List<String> proxies = XmlUtils.getTextForElements(response, "proxy");
        if(proxies == null) {
            throw new InvalidProxyChainTicketValidationException("Invalid proxy chain: No proxy could be retrieved from response. This indicates a problem with CAS validation. Review logs/configuration to find the root cause.");
        } else if(this.allowEmptyProxyChain && proxies.isEmpty()) {
            this.logger.debug("Found an empty proxy chain, permitted by client configuration");
        } else if(this.acceptAnyProxy) {
            this.logger.debug("Client configuration accepts any proxy. It is generally dangerous to use a non-proxied CAS filter specially for protecting resources that require proxy access.");
        } else {
            String[] proxiedList = (String[])proxies.toArray(new String[proxies.size()]);
            if(!this.allowedProxyChains.contains(proxiedList)) {
                this.logger.warn("Proxies received from the CAS validation response are {}. However, none are allowed by allowed proxy chain of the client which is {}", Arrays.toString(proxiedList), this.allowedProxyChains);
                throw new InvalidProxyChainTicketValidationException("Invalid proxy chain: " + proxies.toString());
            }
        }
    }

    public final void setAcceptAnyProxy(boolean acceptAnyProxy) {
        this.acceptAnyProxy = acceptAnyProxy;
    }

    public final void setAllowedProxyChains(ProxyList allowedProxyChains) {
        this.allowedProxyChains = allowedProxyChains;
    }

    protected final boolean isAcceptAnyProxy() {
        return this.acceptAnyProxy;
    }

    protected final boolean isAllowEmptyProxyChain() {
        return this.allowEmptyProxyChain;
    }

    public final void setAllowEmptyProxyChain(boolean allowEmptyProxyChain) {
        this.allowEmptyProxyChain = allowEmptyProxyChain;
    }
}