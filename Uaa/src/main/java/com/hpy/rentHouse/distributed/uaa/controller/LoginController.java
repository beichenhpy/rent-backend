package com.hpy.rentHouse.distributed.uaa.controller;

import DO.UserDo;
import DTO.LoginModelDto;
import com.hpy.rentHouse.distributed.uaa.dao.UserMapper;
import entity.Message;
import entity.ResponseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.AccountException;

/**
 * @author: beichenhpy
 * @Date: 2020/3/1 2:42
 */
@RestController
@RequestMapping("/oauth")
public class LoginController {

    @PostMapping("/login")
    public Message login(@RequestBody LoginModelDto loginModelDto) {
        MultiValueMap<String, Object> paramsMap = new LinkedMultiValueMap<>();
        if ("password".equals(loginModelDto.getGrantType())) {
            paramsMap.set("username", loginModelDto.getUsername());
            paramsMap.set("password", loginModelDto.getPassword());
        } else if ("refresh_token".equals(loginModelDto.getGrantType())) {
            paramsMap.set("refresh_token", loginModelDto.getRefreshToken());
        }
        paramsMap.set("grant_type", loginModelDto.getGrantType());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("user", "123"));
        OAuth2AccessToken token = restTemplate.postForObject("http://localhost:53020/oauth/token", paramsMap, OAuth2AccessToken.class);
        if (token.getValue() == null) {
            return Message.loginFail(ResponseConstant.LOGINFAIL);
        } else {
            return Message.requestSuccess(token);
        }

    }


}






