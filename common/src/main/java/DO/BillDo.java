package DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: beichenhpy
 * @Date: 2020/3/13 17:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDo {
    private String bid;
    private Date createTime;
    private String oid;
    private Double price;
    private Integer renterCheck;
    private Integer ownerCheck;
    private String receipt;
    private PriceInfoDo priceInfoDo;
}
