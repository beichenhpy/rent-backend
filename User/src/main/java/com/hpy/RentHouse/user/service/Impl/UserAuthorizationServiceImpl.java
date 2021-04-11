package com.hpy.RentHouse.user.service.Impl;

import DO.IdCardInfoDo;
import com.hpy.RentHouse.user.dao.IdCardInfoMapper;
import com.hpy.RentHouse.user.dao.UserAuthorizationMapper;
import com.hpy.RentHouse.user.model.IdCardMessage;
import com.hpy.RentHouse.user.service.UserAuthorizationService;
import com.hpy.RentHouse.user.util.OCRUtil;
import com.hpy.RentHouse.user.util.OssUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: beichenhpy
 * @Date: 2020/5/4 19:30
 */
@Service
public class UserAuthorizationServiceImpl implements UserAuthorizationService {
    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private IdCardInfoMapper idCardInfoMapper;
    @Autowired
    private OCRUtil ocrUtil;
    @Autowired
    private UserAuthorizationMapper userAuthorizationMapper;
    //根目录
    @Value("${aliyun.url}")
    private String url;

    private static final Logger logger = LoggerFactory.getLogger(UserAuthorizationServiceImpl.class);

    /**
     * 通过阿里身份证识别api 识别身份证拿到信息
     * 添加身份证信息
     *
     * @param idCardInfoDo 身份证信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addIdCardInfo(String fileName, IdCardInfoDo idCardInfoDo) {
        logger.info("-----------------识别图像 返回身份信息-----------------");
        IdCardMessage idCardMessage = ocrUtil.ocrIdCard(url + fileName);
        idCardInfoDo.setIdNum(idCardMessage.getNum());
        idCardInfoDo.setRealName(idCardMessage.getName());
        /*插入到数据库*/
        idCardInfoMapper.addIdCardInfo(idCardInfoDo);
        //更新状态为已经认证
        Integer isSuccess = userAuthorizationMapper.updateSuccess(idCardInfoDo.getUid());
        if (isSuccess == 0) {
            throw new RuntimeException();
        }
        logger.info("-----------------删除文件-----------------");
        ossUtil.deleteFile(fileName);
    }
}
