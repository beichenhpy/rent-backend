package com.hpy.rentHouse.distributed.rent.config;

import com.hpy.rentHouse.distributed.rent.exception.AuthExceptionEntryPoint;
import com.hpy.rentHouse.distributed.rent.exception.MyAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;


@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true ,jsr250Enabled = true)
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
                .antMatchers("/**").permitAll();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources // 资源id
                .tokenStore(tokenStore)
                .stateless(true);
        resources.authenticationEntryPoint(authExceptionEntryPoint)
                .accessDeniedHandler(myAccessDeniedHandler);
    }

}
