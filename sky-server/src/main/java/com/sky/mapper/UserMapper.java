package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * xml
     * @param user
     */
    void insert(User user);

    Integer countByMap(Map map);
}
