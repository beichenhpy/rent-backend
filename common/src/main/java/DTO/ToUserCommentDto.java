package DTO;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/4/5 20:37
 *
 * 提供给留言服务使用 是接收方的用户信息
 */
@Data
public class ToUserCommentDto {
    private String uid;
    private String nickName;
    private String profilePhoto;
}
