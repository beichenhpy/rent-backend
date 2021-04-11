package com.hpy.RentHouse.user.dao;

import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author: beichenhpy
 * @Date: 2020/5/4 19:32
 */
@Repository
public interface UserAuthorizationMapper {
    /**
     * 更新状态为已经认证
     * @param uid 用户编号
     * @return 返回影响行数
     */
    @Update("update user set success = 1 where success = 0 and uid = #{uid}")
    Integer updateSuccess(String uid);
}
