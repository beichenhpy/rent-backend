package DTO;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/4/27 22:36
 */
@Data
public class StatisticsDto {
    private String sid;
    private String oid;
    private String hid;
    private String uid;
    private Integer electCount;
    private Integer waterCount;
    private String electPrice;
    private String waterPrice;
    private Integer price;
    private String createTime;
}
