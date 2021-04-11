package com.hpy.RentHouse.user.service.Impl;

import DO.IdCardInfoDo;
import DO.UserDo;
import DTO.*;
import com.hpy.RentHouse.user.dao.IdCardInfoMapper;
import com.hpy.RentHouse.user.dao.UserMapper;
import com.hpy.RentHouse.user.model.IdCardMessage;
import com.hpy.RentHouse.user.service.UserService;
import com.hpy.RentHouse.user.util.OCRUtil;
import com.hpy.RentHouse.user.util.OssUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: beichenhpy
 * @Date: 2020/3/11 22:20
 */
@Service
public class UseServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    //根目录
    @Value("${aliyun.url}")
    private String url;


    /**
     * 查询用户全部信息，用于远程调用
     *
     * @param uid uid 用户编号
     * @return 返回用户表现层实体
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserDto findUserDtoByUid(String uid) {
        UserDto userDto = new UserDto();
        //查询
        UserDo userDo = userMapper.findUserAllInfo(uid);
        if (userDo != null){
            //将身份 Do转换成Dto
            IdCardInfoDo idCardInfoDo = userDo.getIdCardInfo();
            IdCardInfoDto idCardInfoDto = null;
            if (idCardInfoDo != null){
                idCardInfoDto = new IdCardInfoDto();
                BeanUtils.copyProperties(idCardInfoDo,idCardInfoDto);
            }
            BeanUtils.copyProperties(userDo,userDto);
            userDto.setIdCardInfo(idCardInfoDto);
            return userDto;
        }
        return null;
    }

    /**
     * 查询自己的基本信息
     *
     * @param uid 用户编号
     * @return 用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserBasicDto findMyInfo(String uid) {
        UserBasicDto userBasicDto = null;
        UserDo userDo = userMapper.findUserBasicInfoByUid(uid);
        if (userDo != null){
            userBasicDto = new UserBasicDto();
            BeanUtils.copyProperties(userDo,userBasicDto);
            userBasicDto.setProfilePhoto(url+userBasicDto.getProfilePhoto());
        }
        return userBasicDto;
    }


    /**
     * 用户编辑自己的用户信息
     *
     * @param profile 用户要修的信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserByUid(String profile,String uid,String type) {
        if ("sex".equals(type)){
            userMapper.updateSexByUid(profile,uid);
        }
        if ("phone".equals(type)){
            userMapper.updatePhoneByUid(profile,uid);
        }
        if ("nickName".equals(type)){
            userMapper.updateNickNameByUid(profile,uid);
        }
    }

    /**
     * 用户上传头像，更新头像路径
     * @param profileImagePath 头像路径
     * @param uid uid
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateProfileImage(String profileImagePath, String uid) {
        Integer update = userMapper.updateProfilePhotoByUid(profileImagePath, uid);
        if (update == 0){
            throw new RuntimeException();
        }
    }


    /**
     * 用户上传自己的电子签名 一般为租客先上传，房东等待
     *
     * @param uid 用户编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateECard(String uid, String fileName) {
        Integer update = userMapper.updateECard(uid, fileName);
        if (update == 0){
            throw new RuntimeException();
        }
    }

    @Override
    public Boolean findNickNameExist(String nickName) {
        return userMapper.selectNickName(nickName) == 1;
    }


    /**
     * 判断用户的电子签名是否存在
     * 用于更新电子签名后，跳转下一页时验证使用
     *
     * @param uid 用户编号
     * @return 返回null/电子签名路径
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String findEcard(String uid) {
        return userMapper.findECard(uid);
    }


    /**
     * 忘记密码
     * @param password 密码
     * @param username 用户名=手机号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void forgetPassword(String password, String username) {
        password = BCrypt.hashpw(password, BCrypt.gensalt());
        userMapper.forgetPassword(password, username);
    }


}




