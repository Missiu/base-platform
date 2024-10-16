package com.example.template.module.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.template.module.domain.dto.auth.UserAuthDTO;
import com.example.template.module.domain.entity.User;
import com.example.template.module.domain.vo.auth.UserAuthVO;

/**
* @author hzh
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-10-04 20:52:02
*/
public interface AuthService extends IService<User> {

    void registerByPassword(UserAuthDTO userAuthDTO);

    UserAuthVO loginByPassword(UserAuthDTO userAuthDTO);

    UserAuthVO authByEmail(UserAuthDTO userAuthDTO);

    void sendVerifyCode(UserAuthDTO userAuthDTO);

    UserAuthVO authByPhone(UserAuthDTO userAuthDTO);

}
