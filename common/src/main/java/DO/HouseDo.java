package DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: beichenhpy
 * @Date: 2020/2/21 20:41
 *
 * 房屋实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseDo {
    private String hid;
    private String province;
    private String city;
    private String village;
    private String address;
    private String building;
    private String unit;
    private String houseNum;
    private String layout;
    private String forward;
    private Double area;
    private Integer isRented;
    private String uid;
    private Integer isCheck;
    private Integer isOnRent;
    private HouseInfoDo houseInfo;
}
