package com.hpy.RentHouse.order.service;

import com.github.pagehelper.PageInfo;
import DTO.OrderDto;

/**
 * @author: beichenhpy
 * @Date: 2020/3/13 18:03
 */

public interface OrderService {

    /**
     * 添加已租信息 租入
     * @param orderDto 已租信息
     */
    void addOrder(OrderDto orderDto);

    /**
     * 删除已租信息 退租
     * @param orderDto 已租信息
     */
    void finishOrder(OrderDto orderDto,Integer isBreak);

    /**
     * 根据房屋编号删除所有的订单
     * @param oid 订单编号
     */
    void delOrdersByOid(String oid,String type);

    /**
     * 取消订单
     * @param oid 订单编号
     */
    void cancelOrder(String oid);

    /**
     * 根据用户编号查询所有订单
     * @param uid 房东/租客用户编号
     * @param type 房东/租客
     * @return 返回订单集合
     */
    PageInfo<OrderDto> findAllOrder(String uid, String type, int page, int size);


    /**
     * 根据订单编号查询订单信息
     * @param oid 订单编号
     * @param type owner/renter
     * @return 返回订单信息
     */
    OrderDto findOrderByOid(String oid, String type);






}
