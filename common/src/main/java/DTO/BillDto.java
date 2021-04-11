package DTO;

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
public class BillDto {
    private String bid;
    private String createTime;
    private String oid;
    private String price;
    private Integer renterCheck;
    private Integer ownerCheck;
    private String receipt;
    private PriceInfoDto priceInfo;
}
