package DTO;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/3/19 21:48
 */
@Data
public class DepositInfoDto {
    private String did;
    private String oid;
    private Integer basicDeposit;
    private Integer keyDeposit;
    private Integer isBack;
    private String renterUid;
    private String ownerUid;
    private String receipt;
    private Integer ownerCheck;
    //是否违约
    private Integer isBreak;
}
