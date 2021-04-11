package com.hpy.RentHouse.order.dao;

import DO.DepositInfoDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/3/19 21:53
 */
@Repository
public interface DepositMapper {

    /**
     * 添加押金详情
     * @param depositInfoDo 押金详情
     */
    @Insert("insert into renterdeposit (did,oid, basicDeposit, keyDeposit,renterUid,ownerUid) VALUES (#{did},#{oid},#{basicDeposit},#{keyDeposit},#{renterUid},#{ownerUid})")
    void addDepositInfo(DepositInfoDo depositInfoDo);

    /**
     * 更新为租客确认已经收到押金退款
     * @param did
     * @return
     */
    @Update("update renterdeposit set isBack = 1 where isBack = 0 and did = #{did}")
    Integer updateBack(String did);

    //房东确认已经退回
    @Update("update renterdeposit set ownerCheck = 1 where ownerCheck = 0 and did = #{did}")
    Integer ownerCheck(String did);
    /**
     * 查询订单对应的押金详情
     * @param oid
     * @return
     */
    @Select("select * from renterdeposit where oid = #{oid}")
    DepositInfoDo findDeposit(String oid);


    @Select("select * from renterdeposit where did = #{did}")
    DepositInfoDo findDepositByDid(String did);

    @Select("select * from renterdeposit where renterUid = #{uid}")
    List<DepositInfoDo> findDepositByUid(String uid);

    @Select("select * from renterdeposit where ownerUid = #{owner}")
    List<DepositInfoDo> findDepositByOwner(String owner);


    @Update("update renterdeposit set receipt = #{receipt} where did = #{did}")
    void updateReceipt(String did,String receipt);

    //违约
    @Update("update renterdeposit set isBreak = 1 where isBreak = 0 and oid = #{oid}")
    Integer breakRule(String oid);

    //删除押金信息
    @Delete("delete from renterdeposit where did = #{did}")
    Integer deleteDeposit(String did);
}
