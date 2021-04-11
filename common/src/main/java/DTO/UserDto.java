package DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: beichenhpy
 * @Date: 2020/3/13 20:35
 * user的提供用户的全部信息，到rent和order服务中在进行转化成 userOrderDto 和userRentDto UserCommentDto
 */
@Data
public class UserDto{
    private String uid;
    private String username;
    private String nickName;
    private String phone;
    private String eCard;
    private Integer rid;
    private String sex;
    private String profilePhoto;
    private Integer success;
    private IdCardInfoDto idCardInfo;
}
