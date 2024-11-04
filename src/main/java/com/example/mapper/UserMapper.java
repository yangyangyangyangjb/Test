package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.example.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> getAll();
}
