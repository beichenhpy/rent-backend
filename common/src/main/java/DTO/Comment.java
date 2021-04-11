package DTO;

import lombok.Data;

/**
 * @author: beichenhpy
 * @Date: 2020/4/26 14:07
 */
@Data
public class Comment {
    private String reciever;
    private String sender;
    private String content;
    private String id;
}
