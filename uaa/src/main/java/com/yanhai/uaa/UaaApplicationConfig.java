package com.yanhai.uaa;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.CompositeFilter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import com.yanhai.core.oauth.resource.UserInfoTokenServices;
import com.yanhai.core.resource.jdbc.JdbcPagingListFactory;
import com.yanhai.core.security.UaaAdminOAuth2WebSecurityExpressionHandler;
import com.yanhai.uaa.authentiaction.AuthzAuthenticationManager;
import com.yanhai.uaa.client.ClientDetailsServices;
import com.yanhai.uaa.client.JdbcClientDetailsServices;
import com.yanhai.uaa.client.bootstrap.ClientAdminBootstrap;
import com.yanhai.uaa.client.bootstrap.ClientsProperties;
import com.yanhai.uaa.oauth.ClientResources;
import com.yanhai.uaa.user.JdbcUserDatabase;
import com.yanhai.uaa.user.JdbcUserServices;
import com.yanhai.uaa.user.UserDatabase;
import com.yanhai.uaa.user.UserServices;
import com.yanhai.uaa.user.bootstrap.UserAdminBootstrap;
import com.yanhai.uaa.user.bootstrap.UsersProperties;

@Configuration
@EnableHystrix
@EnableEurekaClient
public class UaaApplicationConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    protected static class AuthenticationManagerConfig {

        @Bean
        public UserDatabase userDatabase(JdbcTemplate jdbcTemplate) {
            return new JdbcUserDatabase(jdbcTemplate);
        }

        @Bean
        @Primary
        public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder, UserDatabase userDatabase) {
            return new AuthzAuthenticationManager(passwordEncoder, userDatabase);
        }

    }

    @Configuration
    protected static class WebMvcConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/login").setViewName("login");
            registry.addViewController("/oauth/confirm_access").setViewName("access_confirmation");
        }
    }

    @Configuration
    @EnableConfigurationProperties(ClientsProperties.class)
    protected static class ClientsConfig {

        @Bean
        public ClientDetailsServices clientDetailsService(JdbcTemplate jdbcTemplate,
                JdbcPagingListFactory pagingListFactory, PasswordEncoder passwordEncoder) {
            return new JdbcClientDetailsServices(jdbcTemplate, pagingListFactory, passwordEncoder);
        }

        @Bean
        public ClientAdminBootstrap clientsBootstrap(ClientsProperties properties,
                ClientDetailsServices clientDetailsServices) {
            return new ClientAdminBootstrap(properties, clientDetailsServices);
        }
    }

    @Configuration
    @EnableConfigurationProperties(UsersProperties.class)
    protected static class UsersConfig {

        @Bean
        public UserServices userServices(JdbcTemplate jdbcTemplate, JdbcPagingListFactory pagingListFactory,
                PasswordEncoder passwordEncoder) {
            return new JdbcUserServices(jdbcTemplate, pagingListFactory, passwordEncoder);
        }

        @Bean
        public UserAdminBootstrap userBootstrap(UsersProperties usersProperties, UserServices userServices) {
            return new UserAdminBootstrap(usersProperties, userServices);
        }

    }

    @Configuration
    @EnableWebSecurity
    @EnableOAuth2Client
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private OAuth2ClientContext oauth2ClientContext;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.parentAuthenticationManager(authenticationManager);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .antMatcher("/**").httpBasic()
            .and()
                .authorizeRequests()
                    .antMatchers("/", "/login/**", "/css/**", "/webjars/**").permitAll()
                    .antMatchers("/mgmt/**").permitAll()
                    .anyRequest().authenticated()
            .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
            .and()
                .logout().permitAll()
            .and()
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
            // @formatter:on
        }

        @Bean
        @ConfigurationProperties("github")
        public ClientResources github() {
            return new ClientResources();
        }

        @Bean
        public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
            FilterRegistrationBean registration = new FilterRegistrationBean();
            registration.setFilter(filter);
            registration.setOrder(-100);
            return registration;
        }

        private Filter ssoFilter() {
            CompositeFilter filter = new CompositeFilter();
            List<Filter> filters = new ArrayList<>();
            // filters.add(ssoFilter(google(), "/login/google"));
            filters.add(ssoFilter(github(), "/login/github"));
            // filters.add(ssoFilter(facebook(), "/login/facebook"));
            filter.setFilters(filters);
            return filter;
        }

        private Filter ssoFilter(ClientResources client, String path) {
            OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
            OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
            filter.setRestTemplate(template);
            UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(),
                    client.getClient().getClientId());
            tokenServices.setRestTemplate(template);
            filter.setTokenServices(tokenServices);
            return filter;
        }

    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private ClientDetailsServices clientDetailsServices;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetailsServices);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager);

            // 退出登录支持（销毁session）
            endpoints.addInterceptor(new HandlerInterceptorAdapter() {
                @Override
                public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                        ModelAndView modelAndView) throws Exception {
                    if (modelAndView != null && modelAndView.getView() instanceof RedirectView) {
                        RedirectView redirect = (RedirectView) modelAndView.getView();
                        String url = redirect.getUrl();
                        if (url.contains("code=") || url.contains("error=")) {
                            HttpSession session = request.getSession(false);
                            if (session != null) {
                                session.invalidate();
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            security.passwordEncoder(passwordEncoder);
        }
    }

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .requestMatchers().antMatchers("/me", "/users/**", "/oauth/clients/**")
            .and()
                .authorizeRequests()
                    .antMatchers("/me")
                        .authenticated()

                    .antMatchers(HttpMethod.GET, "/oauth/clients")
                        .access("#oauth2.hasAnyScope('clients.admin')")

                    .antMatchers(HttpMethod.GET, "/oauth/clients/*")
                        .access("#oauth2.hasAnyScope('clients.admin','clients.read')")

                    .antMatchers(HttpMethod.POST, "/oauth/clients/**")
                        .access("#oauth2.hasAnyScope('clients.admin','clients.write')")

                    .antMatchers(HttpMethod.PUT, "/oauth/clients/**")
                        .access("#oauth2.hasAnyScope('clients.admin','clients.write')")

                    .antMatchers(HttpMethod.DELETE, "/oauth/clients/**")
                        .access("#oauth2.hasAnyScope('clients.admin')")

                    .antMatchers(HttpMethod.GET,"/users")
                        .access("#oauth2.hasAnyScope('users.admin')")

                    .antMatchers(HttpMethod.GET, "/users/*")
                        .access("#oauth2.hasAnyScope('users.admin','users.read')")

                    .antMatchers(HttpMethod.POST, "/users/**")
                        .access("#oauth2.hasAnyScope('users.admin','users.write')")

                    .antMatchers(HttpMethod.PUT, "/users/**")
                        .access("#oauth2.hasAnyScope('users.admin','users.write')")

                    .antMatchers(HttpMethod.DELETE, "/users/**")
                        .access("#oauth2.hasAnyScope('users.admin')");
            // @formatter:on
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId("uaa");
            resources.expressionHandler(new UaaAdminOAuth2WebSecurityExpressionHandler());
        }
    }
}
