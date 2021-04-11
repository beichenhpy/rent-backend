package DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: beichenhpy
 * @Date: 2020/2/21 21:10
 * 房屋详细信息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseInfoDo {
    private String houseInfoId;
    private String images;
    private String video;
    private Integer isWash;
    private Integer isAir;
    private Integer isRobe;
    private Integer isFridge;
    private Integer isWarm;
    private Integer isBed;
    private String hid;
    private String houseCard;
    private String houseCardNum;
    private Integer waterRecord;
    private Integer carRecord;
    private Integer houseRecord;
}
