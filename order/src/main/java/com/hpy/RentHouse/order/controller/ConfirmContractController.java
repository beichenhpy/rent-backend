package com.hpy.RentHouse.order.controller;

import com.hpy.RentHouse.order.service.ConfirmContractService;
import entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 10:31
 */
@RestController
public class ConfirmContractController {

    @Autowired
    private ConfirmContractService confirmContractService;
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
     * 更新合同路径并签合同
     * @param oid 编号
     * @param type renter/owner
     */
    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/updateContract/{type}")
    public Message updateContract(@RequestParam("oid") String oid, @PathVariable("type")String type) {
        String contract = "contract/"+getUserId()+"_"+oid+"contract.docx";
        if("renter".equals(type)){
            confirmContractService.updateRenterContract(contract, oid);
            confirmContractService.renterConfirm(oid);
        }
        if("owner".equals(type)){
            confirmContractService.updateOwnerContract(contract, oid);
            confirmContractService.ownerConfirm(oid);
        }
        return Message.requestSuccess(null);
    }
}
