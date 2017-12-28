package com.jwcq.config;

/**
 * Created by luotuo on 17-6-26.
 */
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.session.SingleSignOutHandler;
import org.jasig.cas.client.util.AbstractConfigurationFilter;

public final class MySingleSignOutFilter extends AbstractConfigurationFilter {
    private static final MySingleSignOutHandler HANDLER = new MySingleSignOutHandler();
    private AtomicBoolean handlerInitialized = new AtomicBoolean(false);

    public MySingleSignOutFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        if(!this.isIgnoreInitConfiguration()) {
            this.setArtifactParameterName(this.getString(ConfigurationKeys.ARTIFACT_PARAMETER_NAME));
            this.setLogoutParameterName(this.getString(ConfigurationKeys.LOGOUT_PARAMETER_NAME));
            this.setFrontLogoutParameterName(this.getString(ConfigurationKeys.FRONT_LOGOUT_PARAMETER_NAME));
            this.setRelayStateParameterName(this.getString(ConfigurationKeys.RELAY_STATE_PARAMETER_NAME));
            this.setCasServerUrlPrefix(this.getString(ConfigurationKeys.CAS_SERVER_URL_PREFIX));
            HANDLER.setArtifactParameterOverPost(this.getBoolean(ConfigurationKeys.ARTIFACT_PARAMETER_OVER_POST));
            HANDLER.setEagerlyCreateSessions(this.getBoolean(ConfigurationKeys.EAGERLY_CREATE_SESSIONS));
        }

        HANDLER.init();
        this.handlerInitialized.set(true);
    }

    public void setArtifactParameterName(String name) {
        HANDLER.setArtifactParameterName(name);
    }

    public void setLogoutParameterName(String name) {
        HANDLER.setLogoutParameterName(name);
    }

    public void setFrontLogoutParameterName(String name) {
        HANDLER.setFrontLogoutParameterName(name);
    }

    public void setRelayStateParameterName(String name) {
        HANDLER.setRelayStateParameterName(name);
    }

    public void setCasServerUrlPrefix(String casServerUrlPrefix) {
        HANDLER.setCasServerUrlPrefix(casServerUrlPrefix);
    }

    public void setSessionMappingStorage(SessionMappingStorage storage) {
        HANDLER.setSessionMappingStorage(storage);
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        System.out.println("SingleSignOutFilter++++++++++++++++++++");
        if(!this.handlerInitialized.getAndSet(true)) {
            System.out.println("handlerInitialized++++++++++++++++++++");
            HANDLER.init();
        }

        if(HANDLER.process(request, response)) {
            System.out.println("HANDLER.process++++++++++++++++++++");
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    public void destroy() {
    }

    protected static MySingleSignOutHandler getSingleSignOutHandler() {
        return HANDLER;
    }
}
