package DTO;

import lombok.Data;

import java.util.List;

/**
 * @author: beichenhpy
 * @Date: 2020/4/26 14:40
 */
@Data
public class Friends {
    private Integer adminUnRead;
    private List<UserCommentDto> friendInfos;
}
