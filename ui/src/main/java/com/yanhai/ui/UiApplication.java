package com.yanhai.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.netflix.zuul.ZuulFilter;
import com.yanhai.ui.filters.ZuulOAuth2ClientFilter;

@EnableHystrix
@EnableOAuth2Sso
@EnableZuulProxy
@SpringBootApplication
public class UiApplication extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
        http
            .logout()
        .and()
            .authorizeRequests()
            .anyRequest().authenticated()
        .and()
            .csrf()
                .ignoringAntMatchers("/logout/**")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    // @formatter:on
    }

    @Configuration
    protected static class ZuulFilterConfig {

        @Bean
        public ZuulFilter zuulOAuth2ClientFilter() {
            return new ZuulOAuth2ClientFilter();
        }

    }

    @Configuration
    protected static class ErrorConfig {

        @Bean
        public EmbeddedServletContainerCustomizer servletContainerCustomizer() {
            return new EmbeddedServletContainerCustomizer() {
                @Override
                public void customize(ConfigurableEmbeddedServletContainer container) {
                    container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/index.html"));
                }
            };
        }
    }
}
