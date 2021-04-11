package com.hpy.rentHouse.distributed.uaa.config;

import com.hpy.rentHouse.distributed.uaa.exception.AuthExceptionEntryPoint;
import com.hpy.rentHouse.distributed.uaa.exception.MyAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author: beichenhpy
 * @Date: 2020/3/1 14:09
 */

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private AuthExceptionEntryPoint authExceptionEntryPoint;
    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/oauth/token/**").permitAll();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources // 资源id
                .tokenStore(tokenStore)
                .stateless(true);
        resources.authenticationEntryPoint(authExceptionEntryPoint)
                .accessDeniedHandler(myAccessDeniedHandler);
    }


}

