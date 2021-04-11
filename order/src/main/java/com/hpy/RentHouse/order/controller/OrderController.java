package com.hpy.RentHouse.order.controller;

import DTO.BillDto;
import DTO.OrderDto;
import Query.ContractQuery;
import com.github.pagehelper.PageInfo;
import com.hpy.RentHouse.order.service.BillService;
import com.hpy.RentHouse.order.service.OrderService;
import entity.*;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import util.IdWorker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/3/14 13:44
 */
@RestController
public class OrderController {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BillService billService;

    /**
     * 获得上下文中的用户信息
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

    /**
     * 查询用户的所有订单
     * type = owner/renter
     *
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findAll")
    public Message findAllOrder(@RequestParam("type") String type,
                                @RequestParam("page") int page,
                                @RequestParam("size") int size) {

        PageInfo<OrderDto> allOrder =
                orderService.findAllOrder(
                        getUserId(),
                        type,
                        page,
                        size);
        return Message.requestSuccess(allOrder);

    }



    /**
     * 查询订单
     * type = owner/renter
     *
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findOrder")
    public Message findOrder(@RequestParam("oid") String oid, @RequestParam("type") String type) {

        OrderDto orderDto =
                orderService.findOrderByOid(
                        oid,
                        type
                );
        return Message.requestSuccess(orderDto);

    }


    /***********************************************POST***************************************
     * 添加订单信息
     * 为了安全，每次的电子签名都不一样，使用完成后删除
     * 前台要先跳转到添加电子签名操作，然后才能执行此请求，否则报错
     *
     * 需要先将house isRented置为1
     * 将对应房子的出租信息冻结
     * 然后将已租信息添加到数据库
     * 更新缓存
     * 已租信息中的uid 为租客 hid为房屋id
     *
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/addOrder")
    public Message addOrder(@RequestBody OrderDto orderDto) {
        orderDto.setOid(idWorker.nextId() + "");
        orderDto.setRenterUid(getUserId());
        orderService.addOrder(orderDto);
        return Message.requestSuccess(ResponseConstant.ADDORDEROK);
    }


    /***********************************************DELETE*********************************************


     /**
     * 根据房屋编号删除所有的订单
     * @param oid 订单编号
     * @param type renter/owner
     */
    @PreAuthorize("hasAuthority('user')")
    @DeleteMapping("/delOrder/{oid}")
    public Message delOrderByHid(@PathVariable("oid") String oid, @RequestParam("type") String type) {
        orderService.delOrdersByOid(oid, type);
        return Message.requestSuccess(ResponseConstant.DELETE_SUCCESS);
    }

    /**
     * 租客取消订单 同时删除房东的的订单，删除时以contract is null 和 isFinish = 0 和 oid 为条件
     *
     * @param oid 订单编号
     * @return 信息
     */
    @PreAuthorize("hasAuthority('user')")
    @DeleteMapping("/cancel/{oid}")
    public Message cancelOrder(@PathVariable("oid") String oid) {
        orderService.cancelOrder(oid);
        return Message.requestSuccess(ResponseConstant.CANCELOK);
    }

    /***********************************************PUT***************************************************
     *退租信息
     *
     * 将对应房子的出租信息解冻
     * 然后将订单改为完成状态
     * 已租信息中的uid 为租客 hid为房屋id
     * isbreak判断是否提前退租，如果提前退租则不能退还押金
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/finishOrder")
    public Message finishOrder(@RequestBody OrderDto orderDto) {
        //判断是否支付完成 根据用户编号查询到所有账单
        List<BillDto> billDtos = billService.findBillByOid(orderDto.getOid(), getUserId());
        for (BillDto billDto : billDtos) {
            if (billDto.getRenterCheck() == 0) {
                return Message.requestFail(ResponseConstant.NOPAY);
            }
            if (billDto.getOwnerCheck() == 0) {
                return Message.requestFail(ResponseConstant.OWNERNOCHECK);
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date end = sdf.parse(orderDto.getEndTime());
            if(System.currentTimeMillis() - end.getTime() < 0){
                orderService.finishOrder(orderDto,1);
                return Message.breakRule(null);
            }
        } catch (ParseException e) {
            return Message.requestFail(null);
        }
        //放入当前租客的uid用于刷新缓存
        orderDto.setRenterUid(getUserId());
        orderService.finishOrder(orderDto,0);
        return Message.requestSuccess(ResponseConstant.OUTRENTOK);

    }
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/checkAddBill")
    public Message checkAddBill(@RequestParam("lastTime")String lastTime,@RequestParam("billTime")Integer billTime){
        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date last = sdf.parse(lastTime);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(last);
            c1.add(Calendar.MONTH,+billTime);
            System.out.println(c1.getTime());
            if (((c1.getTime().getTime() - now)/3600/1000/24 -7) <= 0){
                return Message.requestSuccess(true);
            }else {
                return Message.requestSuccess(false);
            }
        } catch (ParseException e) {
           return Message.requestFail(null);
        }
    }




}
