package com.example.template.module.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.template.module.domain.entity.User;
import com.example.template.module.service.UserService;
import com.example.template.module.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author hzh
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-10-04 20:52:02
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




