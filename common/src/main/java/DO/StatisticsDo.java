package DO;

import lombok.Data;

import java.util.Date;

/**
 * @author: beichenhpy
 * @Date: 2020/4/27 22:36
 */
@Data
public class StatisticsDo {
    private String sid;
    private String oid;
    private String hid;
    private String uid;
    private Integer electCount;
    private Integer waterCount;
    private Double electPrice;
    private Double waterPrice;
    private Integer price;
    private Date createTime;
}
