package com.huiguli.redisbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huiguli.redisbackend.dto.LoginFormDTO;
import com.huiguli.redisbackend.dto.Result;
import com.huiguli.redisbackend.entity.User;

import javax.servlet.http.HttpSession;

/**
 * @author huiguli
 */
public interface IUserService extends IService<User> {
    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);
}
