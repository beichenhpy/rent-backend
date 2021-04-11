package com.hpy.RentHouse.filestore.dao;

import DO.ContractDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author: beichenhpy
 * @Date: 2020/3/25 21:34
 */
@Repository
public interface FileMapper {
    /**
     * 添加合同信息
     *
     * @param contractDo 合同
     */
    @Insert("insert into contract (oid, ownerName, renterName, " +
            "renterIdNum, ownerIdNum, ownerPhone, renterPhone, " +
            "startTime, endTime, price, deposit, createTime" +
            ",renterUid,ownerUid) VALUES " +
            "(#{oid},#{ownerName},#{renterName}," +
            "#{renterIdNum},#{ownerIdNum},#{ownerPhone},#{renterPhone}," +
            "#{startTime},#{endTime},#{price},#{deposit},#{createTime}" +
            ",#{renterUid},#{ownerUid})")
    void addContract(ContractDo contractDo);

    /**
     * 查询合同
     * @param oid
     * @return
     */
    @Select("select * from contract where oid = #{oid}")
    ContractDo findContractByOid(String oid);

    /**
     * 删除合同信息，在取消订单时使用
     * @param oid
     */
    @Delete("delete from contract where oid = #{oid}")
    Integer deleteContract(String oid);

}
