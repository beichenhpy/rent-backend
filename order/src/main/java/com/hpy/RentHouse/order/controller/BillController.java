package com.hpy.RentHouse.order.controller;

import DTO.BillDto;
import DTO.UnitPriceDto;
import Query.BillQuery;
import Query.ReceiptQuery;
import com.github.pagehelper.PageInfo;
import com.hpy.RentHouse.order.model.PriceCount;
import com.hpy.RentHouse.order.service.BillService;
import entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/3/16 14:17
 */
@RestController
public class BillController {
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

    /***************************************************GET*******************************************
     * 根据订单编号查询所有账单
     * @param type renter/owner
     * @param oid 订单编号
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findAllBill/{oid}")
    public Message findAllBill(@PathVariable("oid") String oid, @RequestParam String type) {
        List<BillDto> billDtoByOid = billService.findBillByOid(oid, type);
        return Message.requestSuccess(billDtoByOid);

    }

    /**
     * 用于在用户查看已租房屋时显示
     * 查询所有未支付的账单的数量
     *
     * @param oid 订单编号
     * @return 返回订单对应的未支付账单数量
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findNoPay/{oid}")
    public Message findNoPay(@PathVariable("oid") String oid) {
        Integer countNoPay = billService.findCountNoPay(oid);
        return Message.requestSuccess(countNoPay);

    }

    @GetMapping("/findBill/{bid}")
    public BillDto findBill(@PathVariable("bid") String bid) {
       return billService.findBillByBid(bid);
    }


    /******************************************************POST*****************************************
     * 房东添加费用详情
     * 通过ownerUid查找到所有的订单，点击添加费用详情，可以拿到oid
     * 添加费用详情 同时添加一个空的账单
     * @param oid 订单编号
     * @param priceCount 费用度数
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/addPriceInfo/{oid}")
    public Message addPriceInfo(@RequestBody PriceCount priceCount, @PathVariable("oid") String oid) {
        billService.addPriceInfo(priceCount, oid, getUserId());
        return Message.requestSuccess(ResponseConstant.INSERT_SUCCESS);
    }



    /***************************************************PUT******************************************************
     * 租客确认支付成功
     * @param billQuery 账单编号
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/renterCheck")
    public Message renterCheck(@RequestBody BillQuery billQuery) {
        billService.renterCheck(billQuery.getBid(), billQuery.getOid());
        return Message.requestSuccess(ResponseConstant.CONFIRMOK);
    }

    /**
     * 房东确认支付成功
     *
     * @param billQuery 账单编号
     * @return 返回信息
     */
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/ownerCheck")
    public Message ownerCheck(@RequestBody BillQuery billQuery) {
        billService.ownerCheck(billQuery.getBid(), billQuery.getOid());
        return Message.requestSuccess(ResponseConstant.CONFIRMOK);
    }


    @PutMapping("/updateReceipt")
    public Message updateReceipt(@RequestBody ReceiptQuery receiptQuery) {
        billService.updateReceipt(receiptQuery.getReceipt(),receiptQuery.getBid());
        return Message.requestSuccess(null);
    }
}
