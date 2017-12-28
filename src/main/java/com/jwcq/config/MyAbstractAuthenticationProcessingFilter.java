package com.jwcq.config;

/**
 * Created by luotuo on 17-6-19.
 */
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

public abstract class MyAbstractAuthenticationProcessingFilter extends GenericFilterBean implements ApplicationEventPublisherAware, MessageSourceAware {
    protected ApplicationEventPublisher eventPublisher;
    protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private AuthenticationManager authenticationManager;
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private RememberMeServices rememberMeServices = new NullRememberMeServices();
    private RequestMatcher requiresAuthenticationRequestMatcher;
    private boolean continueChainBeforeSuccessfulAuthentication = false;
    private SessionAuthenticationStrategy sessionStrategy = new NullAuthenticatedSessionStrategy();
    private boolean allowSessionCreation = true;
    private AuthenticationSuccessHandler successHandler = new MyAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new MySimpleUrlAuthenticationFailureHandler();

    protected MyAbstractAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        this.setFilterProcessesUrl(defaultFilterProcessesUrl);
    }

    protected MyAbstractAuthenticationProcessingFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        Assert.notNull(requiresAuthenticationRequestMatcher, "requiresAuthenticationRequestMatcher cannot be null");
        this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
    }

    public void afterPropertiesSet() {
        Assert.notNull(this.authenticationManager, "authenticationManager must be specified");
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        System.out.println("******************************");
        if(!this.requiresAuthentication(request, response)) {
            System.out.println("Go on");
            chain.doFilter(request, response);
        } else {
            if(this.logger.isDebugEnabled()) {
                this.logger.debug("Request is to process authentication");
            }

            Authentication authResult;
            try {
                System.out.println("Authentication");
                authResult = this.attemptAuthentication(request, response);
                if(authResult == null) {
                    return;
                }
                System.out.println("onAuthentication");
                this.sessionStrategy.onAuthentication(authResult, request, response);
            } catch (InternalAuthenticationServiceException var8) {
                System.out.println("InternalAuthenticationServiceException");
                this.logger.error("An internal error occurred while trying to authenticate the user.", var8);
                this.unsuccessfulAuthentication(request, response, var8);
                return;
            } catch (AuthenticationException var9) {
                System.out.println(var9.getMessage());
                this.unsuccessfulAuthentication(request, response, var9);
                return;
            }
            System.out.println("this.continueChainBeforeSuccessfulAuthentication" + this.continueChainBeforeSuccessfulAuthentication);

            if(this.continueChainBeforeSuccessfulAuthentication) {
                chain.doFilter(request, response);
            }

            this.successfulAuthentication(request, response, chain, authResult);
            chain.doFilter(request, response);
        }
    }

    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return this.requiresAuthenticationRequestMatcher.matches(request);
    }

    public abstract Authentication attemptAuthentication(HttpServletRequest var1, HttpServletResponse var2) throws AuthenticationException, IOException, ServletException;

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);
        this.rememberMeServices.loginSuccess(request, response, authResult);
        if(this.eventPublisher != null) {
            this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }

        this.successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("Authentication request failed: " + failed.toString(), failed);
            this.logger.debug("Updated SecurityContextHolder to contain null Authentication");
            this.logger.debug("Delegating to authentication failure handler " + this.failureHandler);
        }

        this.rememberMeServices.loginFail(request, response);

        this.failureHandler.onAuthenticationFailure(request, response, failed);
    }

    protected AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.setRequiresAuthenticationRequestMatcher(new MyAntPathRequestMatcher(filterProcessesUrl));
    }

    public final void setRequiresAuthenticationRequestMatcher(RequestMatcher requestMatcher) {
        Assert.notNull(requestMatcher, "requestMatcher cannot be null");
        this.requiresAuthenticationRequestMatcher = requestMatcher;
    }

    public RememberMeServices getRememberMeServices() {
        return this.rememberMeServices;
    }

    public void setRememberMeServices(RememberMeServices rememberMeServices) {
        Assert.notNull(rememberMeServices, "rememberMeServices cannot be null");
        this.rememberMeServices = rememberMeServices;
    }

    public void setContinueChainBeforeSuccessfulAuthentication(boolean continueChainBeforeSuccessfulAuthentication) {
        this.continueChainBeforeSuccessfulAuthentication = continueChainBeforeSuccessfulAuthentication;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource required");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    protected boolean getAllowSessionCreation() {
        return this.allowSessionCreation;
    }

    public void setAllowSessionCreation(boolean allowSessionCreation) {
        this.allowSessionCreation = allowSessionCreation;
    }

    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
    }

    protected AuthenticationSuccessHandler getSuccessHandler() {
        return this.successHandler;
    }

    protected AuthenticationFailureHandler getFailureHandler() {
        return this.failureHandler;
    }
}
