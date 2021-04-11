package com.hpy.RentHouse.user.controller;

import DO.IdCardInfoDo;
import DTO.UserBasicDto;
import com.hpy.RentHouse.user.service.UserAuthorizationService;
import com.hpy.RentHouse.user.util.OssUtil;
import entity.Message;
import entity.ResponseConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: beichenhpy
 * @Date: 2020/5/4 19:28
 */
@RestController
public class UserAuthorizationController {

    @Autowired
    private OssUtil ossUtil;

    //图片子目录
    @Value("${file.imgPath}")
    private String imgPath;
    //用户子目录
    @Value("${file.userPath}")
    private String userPath;

    @Autowired
    private UserAuthorizationService userAuthorizationService;
    /**
     * 获得上下文中的用户uid
     */
    private String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2Authentication)) {
            return null;
        }
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        return userAuthentication.getName();
    }

    /*******************************************************POST*********************************************

     /**
     * 注册用户去认证接口 通过username来认证用户
     * @param file 图片
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping(value = "/addIdCardInfoForRegister")
    public Message addIdCardForRegister(MultipartFile file) {
        String filename = ossUtil.upLoadfile(file, imgPath + userPath, getUserId()+"idCard");
        IdCardInfoDo idCardInfoDo = new IdCardInfoDo();
        idCardInfoDo.setUid(getUserId());
        userAuthorizationService.addIdCardInfo(filename, idCardInfoDo);
        //调用身份证识别 插入到身份证信息表中
        return Message.requestSuccess(null);
    }
}
