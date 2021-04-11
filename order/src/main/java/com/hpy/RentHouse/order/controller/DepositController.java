package com.hpy.RentHouse.order.controller;

import DO.OrderDo;
import DTO.DepositInfoDto;
import DTO.DepositReceiptDto;
import DTO.MyDepositDto;
import Query.DepositQuery;
import com.hpy.RentHouse.order.dao.OrderMapper;
import com.hpy.RentHouse.order.service.DepositService;
import DO.DepositInfoDo;
import entity.Message;
import DTO.OrderDto;
import entity.ResponseConstant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/3/19 21:58
 */
@RestController
public class DepositController {
    @Autowired
    private DepositService depositService;
    @Autowired
    private OrderMapper orderMapper;

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
     * 确认押金已经退回
     *
     * @param did 押金编号
     */
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/confirm/{did}")
    public Message confirm(@PathVariable("did") String did) {
        depositService.confirmBack(did);
        return Message.requestSuccess(ResponseConstant.REQUEST_SUCCESS);
    }

    //用于打印文档
    @GetMapping("/findDepositForFile/{oid}")
    public DepositReceiptDto findDepositForFile(@PathVariable("oid") String oid) {
        DepositReceiptDto depositReceiptDto = new DepositReceiptDto();
        DepositInfoDto deposit = depositService.findDeposit(oid);
        OrderDo orderByOid = orderMapper.findOrderByOid(oid);
        BeanUtils.copyProperties(deposit,depositReceiptDto);
        depositReceiptDto.setHid(orderByOid.getHid());
        return depositReceiptDto;
    }


    /**
     * 查询订单对应的押金详情
     *
     * @param oid 订单编号
     * @return 押金详情
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findDeposit/{oid}")
    public Message findDeposit(@PathVariable("oid") String oid) {
        DepositInfoDto depositInfoDto = depositService.findDeposit(oid);
        return Message.requestSuccess(depositInfoDto);

    }

    /**
     * 租客申请退还押金
     * 这个需要订单为完成状态 isFinish = 1
     * 验证通过则给房东发短信 通知退还押金和押金金额
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/askBackDeposit/{did}")
    public Message askBackDeposit(@PathVariable("did") String did) {
        //发送短信给房东
        depositService.askBackDeposit(did);
        return Message.requestSuccess(ResponseConstant.ASKDEPOSITSUCCESS);
    }

    /**
     * 房东确认退还
     */
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/ownerConfirm/{did}")
    public Message ownerConfirm(@PathVariable("did") String did) {
        depositService.ownerCheck(did);
        return Message.requestSuccess(null);
    }

    /**
     * 租客确认退还
     */
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/renterConfirm/{did}")
    public Message renterConfirm(@PathVariable("did") String did) {
        depositService.confirmBack(did);
        return Message.requestSuccess(null);
    }

    /**
     * 查询到用户的所有押金信息
     * @return 押金
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findDeposits")
    public Message findDeposits(){
        List<MyDepositDto> depositByUid = depositService.findDepositByUid(getUserId(),"renter");
        return Message.requestSuccess(depositByUid);
    }
    /**
     * 查询到用户的所有押金信息
     * @return 押金
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/findDepositsOwner")
    public Message findDepositsForOwner(){
        List<MyDepositDto> depositByUid = depositService.findDepositByUid(getUserId(),"owner");
        return Message.requestSuccess(depositByUid);
    }


    //更新收据路径
    @PutMapping("/updateReceiptDeposit")
    public Message updateReceiptDeposit(@RequestBody DepositQuery depositQuery){
        depositService.updateReceipt(depositQuery.getDid(),depositQuery.getReceipt());
        return Message.requestSuccess(null);
    }

    //删除押金
    @PreAuthorize("hasAuthority('user')")
    @DeleteMapping("/deleteDeposit/{did}")
    public Message deleteDeposit(@PathVariable("did")String did){
        depositService.deleteDeposit(did);
        return Message.requestSuccess(null);
    }
}
