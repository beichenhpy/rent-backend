package com.hpy.RentHouse.filestore.controller;

import com.hpy.RentHouse.filestore.service.CreateReceiptService;
import entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 8:59
 */
@RestController
public class CreateReceiptController {

    @Autowired
    private CreateReceiptService createReceiptService;

    //下载收据
    @GetMapping("/getReceipt")
    public Message getReceipt(String uid, String bid){
        String receipt = createReceiptService.printReceipt(bid, uid);
        return Message.requestSuccess(receipt);
    }

    //下载收据
    @GetMapping("/getDeposit")
    public Message getDeposit(String oid){
        String receipt = createReceiptService.printDeposit(oid);
        return Message.requestSuccess(receipt);
    }
}
