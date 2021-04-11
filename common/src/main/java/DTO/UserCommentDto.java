package DTO;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/4/5 20:14
 *
 * 提供给留言服务使用 是发送方的用户信息
 */
@Data
public class UserCommentDto {
    private String uid;
    private String nickName;
    private String profilePhoto;
    private Integer unRead;
}
