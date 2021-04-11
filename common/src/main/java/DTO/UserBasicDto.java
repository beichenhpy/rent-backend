package DTO;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/4/5 21:43
 */
@Data
public class UserBasicDto {
    private String uid;
    private String username;
    private String nickName;
    private String phone;
    private String eCard;
    private Integer rid;
    private String sex;
    private String profilePhoto;
    private Integer success;
}
