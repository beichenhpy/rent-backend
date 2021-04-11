package com.hpy.RentHouse.order.dao;

import DO.OrderDo;
import DTO.OrderDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/3/13 18:01
 * 已租订单持久层
 */
@Repository
public interface OrderMapper {

    /**
     * 添加新的已租信息
     *
     * @param orderDo 已租信息
     */
    @Insert("insert into renterOrder (oid, startTime, endTime, price, deposit, renterUid, hid,ownerUid,months,billTime,contract) VALUES " +
            "(#{oid},#{startTime},#{endTime},#{price},#{deposit},#{renterUid},#{hid},#{ownerUid},#{months},#{billTime},#{contract})")
    void addOrder(OrderDo orderDo);


    /**
     * 根据订单编号 将订单改为订单完成状态 退租使用
     *
     * @param oid 订单编号
     * @return 影响行数
     */
    @Update("update renterOrder set isFinish = 1 where isFinish = 0 and oid = #{oid}")
    Integer updateToIsFinish(String oid);

    /**
     * 租客删除已完成的订单编号的订单
     *
     * @param oid 订单编号
     * @return 影响行数
     */
    @Delete("delete from renterOrder where oid = #{oid} and isFinish = 1")
    Integer delOrdersByOid(String oid);


    /**
     * 取消订单
     *
     * @param oid 订单编号
     * @return 影响行数
     */
    @Delete("delete from renterOrder where oid = #{oid} and isFinish = 0")
    Integer cancelOrder(String oid);

    /**
     * 取消订单同时删除房东的订单
     *
     * @param oid 订单编号
     * @return 影响行数
     */
    @Delete("delete from ownerOrder where oid = #{oid} and isFinish = 0")
    Integer cancelOwnerOrder(String oid);
    /**
     * 房东删除已完成的订单编号的订单
     *
     * @param oid 订单编号
     * @return 影响行数
     */
    @Delete("delete from ownerOrder where oid = #{oid} and isFinish = 1")
    Integer delOrdersByOidOwner(String oid);

    /**
     * 租客查询所有的订单
     *
     * @param renterUid 用户编号
     * @return 返回订单集合
     */
    @Select("select * from renterOrder where renterUid = #{renterUid}")
    List<OrderDo> findAllOrderByRenter(String renterUid);

    /**
     * 房东查询所有的订单
     *
     * @param ownerUid 用户编号
     * @return 返回订单集合
     */
    @Select("select * from ownerOrder where ownerUid = #{ownerUid}")
    List<OrderDo> findAllOrderByOwner(String ownerUid);

    /**
     * 根据订单编号查询订单
     *
     * @param oid 订单编号
     * @return 返回订单
     */
    @Select("select * from renterOrder where oid = #{oid}")
    OrderDo findOrderByOid(String oid);
    /**
     * 根据订单编号查询订单
     *
     * @param oid 订单编号
     * @return 返回订单
     */
    @Select("select * from ownerOrder where oid = #{oid}")
    OrderDo findOwnerOrderByOid(String oid);

    /**
     * 查询所有未完成的订单
     *
     * @return 返回未完成订单的集合
     */
    @Select("select * from renterOrder where isFinish = 0")
    List<OrderDo> findAllUnfinishOrder();

    /**
     * 更新contract字段路径
     * @param contract contract
     * @param oid oid
     * @return
     */
    @Update("update renterOrder set contract = #{contract} where oid = #{oid}")
    void updateContractRenter(String contract,String oid);

    @Update("update ownerOrder set contract = #{contract} where oid = #{oid}")
    void updateContractOwner(String contract,String oid);

    //查询对应合同信息
    @Select("select contract from renterOrder where oid = #{oid}")
    String findContractRenter(String oid);

    //查询对应合同信息
    @Select("select contract from ownerOrder where oid = #{oid}")
    String findContractOwner(String oid);

    //用户支付后就不能取消订单
    @Update("update renterOrder set isFirstPay = 1 where oid = #{oid} and isFirstPay = 0;")
    void updateIsFirstPay(String oid);

    //更新租客签合同
    @Update("update renterorder set renterConfirm = 1 where oid = #{oid} and renterConfirm = 0")
    void renterConfirm(String oid);

    //更新房东签合同
    @Update("update ownerorder set ownerConfirm = 1 where oid = #{oid} and ownerConfirm = 0")
    void ownerConfirm(String oid);
}
