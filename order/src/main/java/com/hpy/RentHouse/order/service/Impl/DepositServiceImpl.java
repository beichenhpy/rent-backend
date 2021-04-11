package com.hpy.RentHouse.order.service.Impl;

import DO.DepositInfoDo;
import DO.OrderDo;
import DTO.Comment;
import DTO.DepositInfoDto;
import DTO.MyDepositDto;
import com.hpy.RentHouse.order.dao.DepositMapper;
import com.hpy.RentHouse.order.dao.OrderMapper;
import com.hpy.RentHouse.order.exception.DepositException;
import com.hpy.RentHouse.order.service.DepositService;
import com.hpy.RentHouse.order.service.feign.CommentFeign;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/3/19 21:56
 */
@Service
public class DepositServiceImpl implements DepositService {
    @Autowired
    private DepositMapper depositMapper;
    @Autowired
    private CommentFeign commentFeign;
    @Autowired
    private OrderMapper orderMapper;
    /**
     * 确认押金已经退回
     *
     * @param did 编号
     */
    @Override
    public void confirmBack(String did) {
        Integer update = depositMapper.updateBack(did);
        if (update == 0) {
            throw new RuntimeException();
        }
    }
    //房东确认退还租金
    @Override
    public void ownerCheck(String did) {
        Integer update = depositMapper.ownerCheck(did);
        if(update == 0){
            throw new RuntimeException();
        }
    }

    /**
     * 查询订单对应的押金详情
     *
     * @param oid 订单编号
     * @return 押金详情
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DepositInfoDto findDeposit(String oid) {
        DepositInfoDo deposit = depositMapper.findDeposit(oid);
        if(deposit !=null){
            DepositInfoDto depositInfoDto = new DepositInfoDto();
            BeanUtils.copyProperties(deposit,depositInfoDto);
            return depositInfoDto;
        }
        return null;
    }

    /**
     * 申请退还押金，给房东发送短信 带上对应的押金金额
     *
     * @param did 订单编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void askBackDeposit(String did) {
        //查询对应的押金金额
        DepositInfoDo depositInfoDo = depositMapper.findDepositByDid(did);
        Comment comment = new Comment();
        comment.setReciever(depositInfoDo.getOwnerUid());
        String content="您好，您有一个出租信息用户正在申请退租，请及时确认";
        comment.setContent(content);
        commentFeign.adminSend(comment);
    }
    //租客查询押金
    @Override
    public List<MyDepositDto> findDepositByUid(String uid,String type) {
        List<DepositInfoDo> depositInfoDos;
        if ("renter".equals(type)){
            depositInfoDos = depositMapper.findDepositByUid(uid);
        }else {
            depositInfoDos = depositMapper.findDepositByOwner(uid);
        }
        List<MyDepositDto> depositInfoDtos = new ArrayList<>();
        if (!depositInfoDos.isEmpty()){
            for (DepositInfoDo depositInfoDo : depositInfoDos) {
                MyDepositDto myDepositDto = new MyDepositDto();
                BeanUtils.copyProperties(depositInfoDo,myDepositDto);
                OrderDo orderByOid = orderMapper.findOrderByOid(depositInfoDo.getOid());
                myDepositDto.setIsFinish(orderByOid.getIsFinish());
                depositInfoDtos.add(myDepositDto);
            }
        }
        return depositInfoDtos;
    }



    @Override
    public void updateReceipt(String did, String receipt) {
        depositMapper.updateReceipt(did, receipt);
    }


    @Override
    public void deleteDeposit(String did) {
        Integer delete = depositMapper.deleteDeposit(did);
        if(delete == 0){
            throw new RuntimeException();
        }
    }
}
