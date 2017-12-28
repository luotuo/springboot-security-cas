package com.jwcq.security;

import com.jwcq.config.*;
import com.jwcq.custom.*;
import com.jwcq.properties.CasProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetailsSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by luotuo on 17-6-26.
 */
@Configuration
@EnableWebSecurity //启用web权限
@EnableGlobalMethodSecurity(prePostEnabled = true) //启用方法验证
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CasProperties casProperties;
    @Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;
    @Resource
    private SessionRegistry sessionRegistry;

    /**定义认证用户信息获取来源，密码校验规则等*/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.authenticationProvider(casAuthenticationProvider());
    }

    /**定义安全策略*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/bindWechat","/user/currentUserInfo","/user/join","/publish/task/editAllowAll","/user/wechatJoin","/publish/task/getQRCodeUrl").permitAll()//访问：/user/currentUserInfo 无需登录认证权限
                .anyRequest().authenticated() //其他所有资源都需要认证，登陆后访问
                .and()
                .logout()
                .permitAll();//定义logout不需要验证
                //.and().sessionManagement().invalidSessionUrl("/login").maximumSessions(-1).maxSessionsPreventsLogin(true).sessionRegistry(sessionRegistry());
                //.and().sessionManagement().invalidSessionStrategy(simpleRedirectInvalidSessionStrategy()).invalidSessionUrl("/login").maximumSessions(-1).maxSessionsPreventsLogin(true).sessionRegistry(sessionRegistry());
                //.and()
                //.and()
                //.formLogin();//使用form表单登录
        http.sessionManagement().sessionFixation().none();
        http.sessionManagement().sessionAuthenticationFailureHandler(new MySimpleUrlAuthenticationFailureHandler());
        http.sessionManagement().maximumSessions(-1).sessionRegistry(sessionRegistry());

        http.exceptionHandling().authenticationEntryPoint(casAuthenticationEntryPoint());
        http.addFilterAt(myCasAuthenticationFilter(), CasAuthenticationFilter.class);
        http.addFilterAt(casLogoutFilter(), LogoutFilter.class);
        http.addFilterAt(singleSignOutFilter(), MyCasAuthenticationFilter.class);
//                .and()
//                .addFilterBefore(myCasAuthenticationFilter(), CasAuthenticationFilter.class)
//                .addFilterBefore(casLogoutFilter(), LogoutFilter.class)
//                .addFilterBefore(singleSignOutFilter(), MyCasAuthenticationFilter.class);

        http.csrf().disable(); //禁用CSRF
        //http.addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class);
        http.addFilterAt(myFilterSecurityInterceptor, FilterSecurityInterceptor.class);
        http.addFilterAt(sessionInfomationExpiredStategy(), ConcurrentSessionFilter.class);
        //http.sessionManagement().maximumSessions(1).expiredUrl("/login");
        //http.addFilterAt(usernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        //http.addFilterBefore(sessionInfomationExpiredStategy(), ConcurrentSessionFilter.class);

        //http.addFilterBefore(expiredSessionFilter(), SessionManagementFilter.class);
        //session并发控制过滤器
        //http.addFilterAt(new ConcurrentSessionFilter(sessionRegistry,sessionInformationExpiredStrategy()),ConcurrentSessionFilter.class);
    }

    //SpringSecurity内置的session监听器
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }


    private Filter sessionInfomationExpiredStategy() {
        ConcurrentSessionFilter concurrentSessionFilter = new ConcurrentSessionFilter(sessionRegistry(), new MySessionInformationExpiredStrategy());
        return concurrentSessionFilter;
    }

    @Bean
    public MySimpleRedirectInvalidSessionStrategy simpleRedirectInvalidSessionStrategy() {
        return new MySimpleRedirectInvalidSessionStrategy("/login");
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**认证的入口*/
    @Bean
    public MyCasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        MyCasAuthenticationEntryPoint casAuthenticationEntryPoint = new MyCasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLoginUrl(casProperties.getCasServerLoginUrl());
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }

    /**指定service相关信息*/
    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(casProperties.getAppServerUrl() + casProperties.getAppLoginUrl());
        serviceProperties.setAuthenticateAllArtifacts(true);
        return serviceProperties;
    }

    /**CAS认证过滤器*/
    @Bean
    public MyCasAuthenticationFilter myCasAuthenticationFilter() throws Exception {
        MyCasAuthenticationFilter casAuthenticationFilter = new MyCasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setFilterProcessesUrl(casProperties.getAppLoginUrl());
        casAuthenticationFilter.setAuthenticationFailureHandler(new MySimpleUrlAuthenticationFailureHandler());
        casAuthenticationFilter.setAuthenticationSuccessHandler(new MyAuthenticationSuccessHandler());
        casAuthenticationFilter.setServiceProperties(serviceProperties());
        casAuthenticationFilter.setAuthenticationDetailsSource(new ServiceAuthenticationDetailsSource(serviceProperties()));
        //casAuthenticationFilter.setAuthenticationDetailsSource(customUserDetailsService());
        return casAuthenticationFilter;
    }

    /**cas 认证 Provider*/
    @Bean
    public MyCasAuthenticationProvider casAuthenticationProvider() {
        MyCasAuthenticationProvider casAuthenticationProvider = new MyCasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(customUserDetailsService());
        //casAuthenticationProvider.setUserDetailsService(customUserDetailsService()); //这里只是接口类型，实现的接口不一样，都可以的。
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        //casAuthenticationProvider.setTicketValidator(cas20ProxyTicketValidator());
        casAuthenticationProvider.setKey("casAuthenticationProviderKey");
        return casAuthenticationProvider;
    }

    /**用户自定义的AuthenticationUserDetailsService*/
    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> customUserDetailsService(){
        return new CustomUserDetailsService();
    }

    @Bean
    public MyCas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new MyCas20ServiceTicketValidator(casProperties.getCasServerUrl());
    }

    /**单点登出过滤器*/
    @Bean
    public MySingleSignOutFilter singleSignOutFilter() {
        MySingleSignOutFilter singleSignOutFilter = new MySingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(casProperties.getCasServerUrl());
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    /**请求单点退出过滤器*/
    @Bean
    public MyLogoutFilter casLogoutFilter() {
        MyLogoutFilter mylogoutFilter = new MyLogoutFilter(casProperties.getCasServerLogoutUrl(), new SecurityContextLogoutHandler());
        mylogoutFilter.setFilterProcessesUrl(casProperties.getAppLogoutUrl());
        return mylogoutFilter;
    }
}


