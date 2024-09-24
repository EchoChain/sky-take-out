package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/20 19:50
 * @comment
 */
@Mapper
public interface UserMapper {
    // 不能加 AutoFill 注解 不符合 INSERT 操作
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into user(openid, name, phone, sex, id_number, avatar, create_time) values (#{openid}, #{name}, #{phone}, #{sex}, #{idNumber}, #{avatar}, #{createTime})")
    void insert(User user);

    @Select("select * from user where openid = #{openId}")
    User getByOpenId(String openId);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);
}
