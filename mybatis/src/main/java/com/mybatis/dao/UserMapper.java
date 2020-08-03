package com.mybatis.dao;

import com.mybatis.pojo.User;

public interface UserMapper {
    User getUserById(String id);
}
