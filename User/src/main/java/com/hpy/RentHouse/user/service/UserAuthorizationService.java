package com.hpy.RentHouse.user.service;

import DO.IdCardInfoDo;

/**
 * @author: beichenhpy
 * @Date: 2020/5/4 19:29
 */

public interface UserAuthorizationService {
    /**
     * 添加身份证信息
     * @param idCardInfoDo 身份证信息
     */
    void addIdCardInfo(String fileName, IdCardInfoDo idCardInfoDo);
}
