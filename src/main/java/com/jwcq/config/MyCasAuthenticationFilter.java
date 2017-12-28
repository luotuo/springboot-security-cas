package com.jwcq.config;

/**
 * Created by luotuo on 17-6-19.
 */
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jwcq.custom.UserInfo;
import com.jwcq.security.SecurityConfig;
import com.jwcq.service.UserService;
import com.jwcq.user.entity.User;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.TicketValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
public class MyCasAuthenticationFilter extends MyAbstractAuthenticationProcessingFilter {
    public static final String CAS_STATEFUL_IDENTIFIER = "_const_cas_assertion_";
    public static final String CAS_STATELESS_IDENTIFIER = "_cas_stateless_";
    private RequestMatcher proxyReceptorMatcher;
    private ProxyGrantingTicketStorage proxyGrantingTicketStorage;
    private String artifactParameter = "ticket";
    private boolean authenticateAllArtifacts;
    private AuthenticationFailureHandler proxyFailureHandler = new SimpleUrlAuthenticationFailureHandler();


    @Autowired
    private SecurityConfig securityConfig;
    @Autowired
    private UserService userService;

    public MyCasAuthenticationFilter() {
        super("/cas-server/login");
        this.setAuthenticationFailureHandler(new MySimpleUrlAuthenticationFailureHandler());
    }

    protected final void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        boolean continueFilterChain = this.proxyTicketRequest(this.serviceTicketRequest(request, response), request);
        if(!continueFilterChain) {
            super.successfulAuthentication(request, response, chain, authResult);
        } else {
            if(this.logger.isDebugEnabled()) {
                this.logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
            }

            SecurityContextHolder.getContext().setAuthentication(authResult);
            if(this.eventPublisher != null) {
                this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
            }

            chain.doFilter(request, response);
        }
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        System.out.println("+++++++++++++++++++++++");

        if(this.proxyReceptorRequest(request)) {
            this.logger.debug("Responding to proxy receptor request");
            CommonUtils.readAndRespondToProxyReceptorRequest(request, response, this.proxyGrantingTicketStorage);
            return null;
        } else {
            boolean serviceTicketRequest = this.serviceTicketRequest(request, response);
            System.out.println("serviceTicketRequest == " + serviceTicketRequest);

            //String username = serviceTicketRequest?"_cas_stateful_":"_cas_stateless_";  _const_cas_assertion_
            String username = serviceTicketRequest?CAS_STATEFUL_IDENTIFIER:CAS_STATELESS_IDENTIFIER;
            String password = this.obtainArtifact(request);
            System.out.println("password == " + password);
            if(password == null) {
                this.logger.debug("Failed to obtain an artifact (cas ticket)");
                password = "";
            }

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
            System.out.println("adsfasdfadsfasdfadfasdfasdf");
//            SecurityContextImpl securityContextImpl = (SecurityContextImpl) request
//                    .getSession().getAttribute("SPRING_SECURITY_CONTEXT");
//            if (securityContextImpl != null) {
//                String userLoginName = securityContextImpl.getAuthentication().getName();
//                User user = userService.getUserByLoginName(userLoginName);
//                if (user != null)
//                    checkUserLoginOrNot(user, request);
//            }
            Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);
            return  authentication;
            //return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    private void checkUserLoginOrNot(User u, HttpServletRequest request) throws BadCredentialsException {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(u.getEmail());
        userInfo.setName(u.getEmail());
        userInfo.setId(u.getId());
        SessionRegistry sessionRegistry = securityConfig.sessionRegistry();
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        List<SessionInformation> allSessions = sessionRegistry.getAllSessions(userInfo, false);
        if (allSessions.isEmpty())
            return;
        String sessionId = request.getSession().getId();
        boolean hasLogin = false;
        for (int j = 0; j < allPrincipals.size(); j++) {
            UserInfo user = (UserInfo) allPrincipals.get(j);
            if (user.getId().equals(Long.valueOf(userInfo.getId()))) {
//                for (SessionInformation s : allSessions) {
//                    if (s.getSessionId().equals(sessionId)) {
//                        hasLogin = false;
//                        break;
//                    }
//                }
                hasLogin = true;
                break;
            }
//            if (!hasLogin)
//                break;
        }
        if (hasLogin)
            throw new BadCredentialsException("1");
    }

    protected String obtainArtifact(HttpServletRequest request) {
        return request.getParameter(this.artifactParameter);
    }

    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        boolean serviceTicketRequest = this.serviceTicketRequest(request, response);
        boolean result = serviceTicketRequest || this.proxyReceptorRequest(request) || this.proxyTicketRequest(serviceTicketRequest, request);
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("requiresAuthentication = " + result);
        }

        return result;
    }

    public final void setProxyAuthenticationFailureHandler(AuthenticationFailureHandler proxyFailureHandler) {
        Assert.notNull(proxyFailureHandler, "proxyFailureHandler cannot be null");
        this.proxyFailureHandler = proxyFailureHandler;
    }

    public final void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        super.setAuthenticationFailureHandler(new MyCasAuthenticationFilter.CasAuthenticationFailureHandler(failureHandler));
    }

    public final void setProxyReceptorUrl(String proxyReceptorUrl) {
        this.proxyReceptorMatcher = new AntPathRequestMatcher("/**" + proxyReceptorUrl);
    }

    public final void setProxyGrantingTicketStorage(ProxyGrantingTicketStorage proxyGrantingTicketStorage) {
        this.proxyGrantingTicketStorage = proxyGrantingTicketStorage;
    }

    public final void setServiceProperties(ServiceProperties serviceProperties) {
        this.artifactParameter = serviceProperties.getArtifactParameter();
        this.authenticateAllArtifacts = serviceProperties.isAuthenticateAllArtifacts();
    }

    private boolean serviceTicketRequest(HttpServletRequest request, HttpServletResponse response) {
        boolean result = super.requiresAuthentication(request, response);
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("serviceTicketRequest = " + result);
        }

        return result;
    }

    private boolean proxyTicketRequest(boolean serviceTicketRequest, HttpServletRequest request) {
        if(serviceTicketRequest) {
            return false;
        } else {
            boolean result = this.authenticateAllArtifacts && this.obtainArtifact(request) != null && !this.authenticated();
            if(this.logger.isDebugEnabled()) {
                this.logger.debug("proxyTicketRequest = " + result);
            }

            return result;
        }
    }

    private boolean authenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private boolean proxyReceptorRequest(HttpServletRequest request) {
        boolean result = this.proxyReceptorConfigured() && this.proxyReceptorMatcher.matches(request);
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("proxyReceptorRequest = " + result);
        }

        return result;
    }

    private boolean proxyReceptorConfigured() {
        boolean result = this.proxyGrantingTicketStorage != null && this.proxyReceptorMatcher != null;
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("proxyReceptorConfigured = " + result);
        }

        return result;
    }

    private class CasAuthenticationFailureHandler implements AuthenticationFailureHandler {
        private final AuthenticationFailureHandler serviceTicketFailureHandler;

        public CasAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
            Assert.notNull(failureHandler, "failureHandler");
            this.serviceTicketFailureHandler = failureHandler;
        }

        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            if(MyCasAuthenticationFilter.this.serviceTicketRequest(request, response)) {
                this.serviceTicketFailureHandler.onAuthenticationFailure(request, response, exception);
            } else {
                MyCasAuthenticationFilter.this.proxyFailureHandler.onAuthenticationFailure(request, response, exception);
            }

        }
    }
}

