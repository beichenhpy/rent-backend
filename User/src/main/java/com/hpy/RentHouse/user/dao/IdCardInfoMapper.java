package com.hpy.RentHouse.user.dao;

import DO.IdCardInfoDo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 14:39
 */
@Repository
public interface IdCardInfoMapper {
    /**********************************idCardInfo表***************************/
    /**
     * 插入新的身份证信息
     * @param idCardInfoDo 身份证信息实体
     */
    @Insert("insert into idcardinfo (idNum,realName,uid) VALUES " +
            "(#{idNum},#{realName},#{uid})")
    void addIdCardInfo(IdCardInfoDo idCardInfoDo);


    /**
     * 查询自己的认证信息
     * @param uid 用户编号
     * @return 返回认证信息
     */
    @Select("select * from idcardinfo where uid = #{uid}")
    IdCardInfoDo findIdCardInfo(String uid);
}
