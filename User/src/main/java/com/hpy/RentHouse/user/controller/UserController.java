package com.hpy.RentHouse.user.controller;

import DO.IdCardInfoDo;
import DO.UserDo;
import DTO.*;
import Query.RegisterQuery;
import Query.SignQuery;
import Query.UpdateUserQuery;
import com.hpy.RentHouse.user.dao.UserMapper;
import com.hpy.RentHouse.user.service.UserService;
import com.hpy.RentHouse.user.service.feign.RedisFeign;
import com.hpy.RentHouse.user.util.OssUtil;
import entity.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: beichenhpy
 * @Date: 2020/3/11 22:20
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private RedisFeign redisFeign;
    //图片子目录
    @Value("${file.imgPath}")
    private String imgPath;
    //用户子目录
    @Value("${file.userPath}")
    private String userPath;

    @Value("${aliyun.url}")
    private String url;

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

    /*********************************************GET*******************************************

     /**
     * 用于远程调用
     * @param uid uid
     * @return 返回uid
     */
    @GetMapping("/find/{uid}")
    public UserDto findUserById(@PathVariable("uid") String uid) {
        return userService.findUserDtoByUid(uid);
    }


    /**
     * 查询本用户的信息
     *
     * @return 信息
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findMyInfo")
    public Message findUserByUid() {
        UserBasicDto user = userService.findMyInfo(getUserId());
        return Message.requestSuccess(user);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findNickNameExist")
    public Message findNickNameExist(@RequestParam("nickName")String nickName){
        return Message.requestSuccess(userService.findNickNameExist(nickName));
    }

    /**
     * 判断用户的电子签名是否存在
     * 用于更新电子签名后，跳转下一页时验证使用
     *
     * @return msg
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findECard")
    public Message findECard() {
        String ecard = userService.findEcard(getUserId());
        if (StringUtils.isEmpty(ecard)) {
            return Message.requestSuccess(0);
        }
        return Message.requestSuccess(1);
    }

    /********************************************PUT*************************************************
     * 根据类型判断修改哪种信息
     * @param updateUserQuery :type =  password sex phone nickName
     * @return 返回封装信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/updateUser")
    public Message updateUser(@RequestBody UpdateUserQuery updateUserQuery) {
        userService.updateUserByUid(updateUserQuery.getProfile(), getUserId(), updateUserQuery.getType());
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }

    /**
     * 将用户的电子签名置为空
     *
     * @param uid uid
     */
    @PutMapping("/updateECardToNull/{uid}")
    public void updateECardToNull(@PathVariable("uid") String uid) {
        userService.updateECard(uid, null);
    }

    //用户忘记密码
    @PutMapping("/forgetPassword")
    public Message resetPassword(@RequestBody RegisterQuery registerQuery) {
        //从redis中获取验证码
        String checkCodeRedis = redisFeign.get(registerQuery.getUsername());
        //不存在
        if (checkCodeRedis == null || checkCodeRedis.isEmpty()) {
            return Message.requestFail(ResponseConstant.CODE_WRONG);
        }
        //验证码错误
        if (!checkCodeRedis.equals(registerQuery.getVerCode())) {
            return Message.requestFail(ResponseConstant.CODE_WRONG);
        }
        userService.forgetPassword(registerQuery.getPassword(),registerQuery.getUsername());
        return Message.requestSuccess(ResponseConstant.UPDATE_SUCCESS);
    }


    /**
     * 用户更改自己的头像
     *
     * @param file
     * @return
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/updateProfilePhoto")
    public Message updateProfilePhoto(MultipartFile file) {
        //查询用户之前的头像
        String profilePhoto = ossUtil.upLoadfile(file, imgPath + userPath, getUserId()+"profilePhoto");
        userService.updateProfileImage(profilePhoto, getUserId());
        return Message.requestSuccess(ResponseConstant.UPLOADOK);
    }


    /**
     * 用户上传自己的电子签名,
     * 使用uid+eCard为名字保存起来
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/updateECard")
    public Message updateECard(MultipartFile file) {
        String filename = ossUtil.upLoadfile(file, imgPath + userPath, getUserId()+"eCard");
        userService.updateECard(getUserId(), filename);
        return Message.requestSuccess(ResponseConstant.UPLOADOK);
    }



}
