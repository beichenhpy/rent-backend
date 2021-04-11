package com.hpy.rentHouse.distributed.uaa.config;
import com.hpy.rentHouse.distributed.uaa.exception.MyWebResponseExceptionTranslator;
import com.hpy.rentHouse.distributed.uaa.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author beichenhpy
 * Oauth2 的认证服务配置适配器
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    //数据库查询用户信息
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    //密码模式
    @Autowired
    private AuthenticationManager authenticationManager;
    //bc加密
    @Autowired
    private PasswordEncoder passwordEncoder;
    //token生成配置信息
    @Autowired
    private TokenStore tokenStore;
    //jwt
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    //客户端信息
    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private MyWebResponseExceptionTranslator myWebResponseExceptionTranslator;
    // 配置客户端详细信息
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("user")
                .authorizedGrantTypes("client_credentials","password","refresh_token")
                .scopes("all")
                .authorities("user","admin")
                .autoApprove(true)
                .secret(passwordEncoder.encode("123"));

    }

    //令牌管理服务
    @Bean
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices service=new DefaultTokenServices();
        //设置客户端信息
        service.setClientDetailsService(clientDetailsService);
        //设置支持refresh_token
        service.setSupportRefreshToken(true);
        service.setTokenStore(tokenStore);
        //令牌增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        //jwt存储Token设置
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter));
        service.setTokenEnhancer(tokenEnhancerChain);
        //access_token过期时间
        service.setAccessTokenValiditySeconds(25920);
        //refresh_token过期时间
        service.setRefreshTokenValiditySeconds(259200);
        return service;
    }

    //    配置授权服务器端点
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                //配置token信息
                .tokenServices(tokenService())
                // 密码管理模式
                .authenticationManager(authenticationManager)
                //配置数据库查询user信息
                .userDetailsService(userDetailsService);
        //异常处理
        endpoints.exceptionTranslator(myWebResponseExceptionTranslator);
    }


    //令牌访问端点的安全策略
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // /oauth/token_key  公开
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                // 允许通过表单认证，申请令牌
                .allowFormAuthenticationForClients();
    }
}
