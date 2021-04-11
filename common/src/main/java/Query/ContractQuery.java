package Query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: beichenhpy
 * @Date: 2020/4/1 21:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractQuery {
    private String contract;
    private String oid;
    private String ownerUid;
    private String renterUid;
}
