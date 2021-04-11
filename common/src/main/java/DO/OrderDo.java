package DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: beichenhpy
 * @Date: 2020/2/23 22:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDo {
    private String oid;
    private Date startTime;
    //租期
    private Integer months;
    private Date endTime;
    private Integer price;
    private Integer deposit;
    private String contract;
    private String renterUid;
    private String hid;
    //多久交一次费用 和onRentInfo中的time一致
    private Integer billTime;
    //是否退租
    private Integer isFinish;
    private String ownerUid;
    //支付
    private Integer isFirstPay;
    //是否签合同
    private Integer renterConfirm;
    private Integer ownerConfirm;
}
