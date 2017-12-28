package com.jwcq.config;
/**
 * Created by luotuo on 17-6-19.
 */
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public MyAuthenticationSuccessHandler() {
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        if(savedRequest == null) {
            //super.onAuthenticationSuccess(request, response, authentication);
            handle(request, response, authentication);
            super.clearAuthenticationAttributes(request);
        } else {
            String targetUrlParameter = this.getTargetUrlParameter();
            if(!this.isAlwaysUseDefaultTargetUrl() && (targetUrlParameter == null || !StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
                this.clearAuthenticationAttributes(request);
                String targetUrl = savedRequest.getRedirectUrl();
                this.logger.debug("Redirecting to DefaultSavedRequest Url: " + targetUrl);
                //this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
            } else {
                this.requestCache.removeRequest(request, response);
                //super.onAuthenticationSuccess(request, response, authentication);
                handle(request, response, authentication);
                super.clearAuthenticationAttributes(request);
            }
        }
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = this.determineTargetUrl(request, response);
        if(response.isCommitted()) {
            this.logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
        } else {
            //redirectStrategy.sendRedirect(request, response, targetUrl);
        }
    }

}
