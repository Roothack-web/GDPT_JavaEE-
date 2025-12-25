package com.example.ordersystem.mapper;

import com.example.ordersystem.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    // UserMapper.java 中添加
    @Insert("INSERT INTO user(username, password, email, phone, roles) " +
            "VALUES(#{username}, #{password}, #{email}, #{phone}, 'ROLE_USER')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}
