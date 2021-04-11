package DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/2/21 21:10
 * 房屋详细信息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseInfoDto {
    private String houseInfoId;
    private List<String> images;
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
