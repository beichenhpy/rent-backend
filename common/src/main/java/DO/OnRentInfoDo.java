package DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: beichenhpy
 * @Date: 2020/2/23 21:30
 *
 * 出租租房信息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnRentInfoDo {
    private String hid;
    private String title;
    private Integer price;
    //月押金
    private Integer deposit;
    //钥匙押金
    private Integer keyDeposit;
    private Integer time;
    private String rule;
    private String uid;
    private Integer isFrozen;
    private Integer isOnRent;
    private String image;
    //城市和省份 拓展查询方便
    private String province;
    private String city;
    private Date createTime;
    private String detailPosition;
    private String introduction;
    private Integer leastTime;
}
