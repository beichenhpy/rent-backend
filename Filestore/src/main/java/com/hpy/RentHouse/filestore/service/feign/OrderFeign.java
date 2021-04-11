package com.hpy.RentHouse.filestore.service.feign;

import DTO.BillDto;
import DTO.DepositReceiptDto;
import Query.DepositQuery;
import Query.ReceiptQuery;
import entity.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author: beichenhpy
 * @Date: 2020/3/25 21:21
 */
@FeignClient("order-service")
public interface OrderFeign {

    //查询账单
    @GetMapping("/findBill/{bid}")
    BillDto findBill(@PathVariable("bid") String bid);

    //更新租金收据路径
    @PutMapping("/updateReceipt")
    Message updateReceipt(@RequestBody ReceiptQuery receiptQuery);

    //查询押金信息
    @GetMapping("/findDepositForFile/{oid}")
    DepositReceiptDto findDepositForFile(@PathVariable("oid") String oid);

    //更新押金收据路径
    @PutMapping("/updateReceiptDeposit")
    Message updateReceiptDeposit(@RequestBody DepositQuery depositQuery);

}
