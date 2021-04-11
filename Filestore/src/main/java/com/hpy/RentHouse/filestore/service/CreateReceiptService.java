package com.hpy.RentHouse.filestore.service;

/**
 * @author: beichenhpy
 * @Date: 2020/5/3 8:56
 */

public interface CreateReceiptService {

    String printReceipt(String bid,String uid);

    String printDeposit(String did);
}
