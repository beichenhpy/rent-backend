package DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: beichenhpy
 * @Date: 2020/3/24 20:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractDo {
    private String oid;
    private String ownerName;
    private String renterName;
    private String ownerIdNum;
    private String renterIdNum;
    private String ownerPhone;
    private String renterPhone;
    private String startTime;
    private String endTime;
    private String price;
    private String deposit;
    private String createTime;
    private String ownerUid;
    private String renterUid;
}
