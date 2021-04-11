package com.hpy.RentHouse.order.service;

import DO.BillDo;
import DO.OrderDo;
import DTO.*;
import com.alibaba.fastjson.JSON;
import com.hpy.RentHouse.order.dao.BillMapper;
import com.hpy.RentHouse.order.dao.OrderMapper;
import com.hpy.RentHouse.order.service.feign.CommentFeign;
import com.hpy.RentHouse.order.service.feign.UserFeign;
import entity.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/3/21 23:35
 */
@Component
public class task {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private BillMapper billMapper;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private CommentFeign commentFeign;
    /**
     * 定时任务，每天查询一次 从所有未完成的订单中
     * 查询到所有的账单为 createTime = nowTime - 1month - 7的账单，找到对应的oid 集合
     * 然后根据oid集合 找到对应的renterUid和ownerUid
     * 找到他们的phone 发短信/cid 推送信息
     *
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void noticeToPay() {
        try {
            //查询所有未完成订单
            List<OrderDo> orderDos = orderMapper.findAllUnfinishOrder();
            for (OrderDo orderDo : orderDos) {
                //根据特定创建日期查询所有账单
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.MONTH,-orderDo.getBillTime());
                calendar.add(Calendar.DAY_OF_YEAR,-7);
                Date time = formatter.parse(formatter.format(calendar.getTime()));
                BillDo billDo = billMapper.findBillByCreateTime(time, orderDo.getOid());
                if (billDo != null){
                    //只要有bill就可以发送短信/通知了
                    //拿到ownerUid和renterUid
                    UserDto renter = userFeign.findUserById(orderDo.getRenterUid());
                    UserDto owner = userFeign.findUserById(orderDo.getOwnerUid());
                    if (renter == null || owner == null){
                        throw new RuntimeException();
                    }
                    //拿到对象发送短信/推送
                    Comment renterComment = new Comment();
                    Comment ownerComment = new Comment();
                    renterComment.setReciever(renter.getUid());
                    ownerComment.setReciever(owner.getUid());
                    String contentOwner="您租出的房屋已经该缴费了，请及时查看缴费情况，并及时创建新的账单哦";
                    String contentRenter = "您租下的房屋已经该缴纳费用了，请及时确认缴费状态";
                    renterComment.setContent(contentRenter);
                    ownerComment.setContent(contentOwner);
                    commentFeign.adminSend(ownerComment);
                    commentFeign.adminSend(renterComment);
                    //结束
                    break;
                }
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
