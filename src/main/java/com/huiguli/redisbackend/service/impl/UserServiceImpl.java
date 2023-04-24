package com.huiguli.redisbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huiguli.redisbackend.dto.LoginFormDTO;
import com.huiguli.redisbackend.dto.Result;
import com.huiguli.redisbackend.dto.UserDTO;
import com.huiguli.redisbackend.entity.User;
import com.huiguli.redisbackend.mapper.UserMapper;
import com.huiguli.redisbackend.service.IUserService;
import com.huiguli.redisbackend.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.huiguli.redisbackend.constant.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * @author huiguli
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            //2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误");
        }
        //3.符合，生成验证码(利用hutool工具)
        String code = RandomUtil.randomNumbers(6);
        //4.保存验证码到session中
        session.setAttribute("code", code);
        //5.发送验证码(需调用第三方平台)
        log.debug("模拟发送验证码成功，验证码：{}",code);
        //返回ok
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        String phone = loginForm.getPhone();
        // 1.校验密码
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        // 2.校验验证码
        Object cachecode = session.getAttribute("code");
        String code = loginForm.getCode();
        if (cachecode == null || !cachecode.toString().equals(code)) {
            // 3.验证码不一致，报错
            return Result.fail("验证码错误");
        }
        // 4.一致，根据手机号查询用户 select * from tb_user where phone = ?;
        User user = query().eq("phone", phone).one();
        // 5.判断用户是否存在
        if (user == null) {
            // 6.用户不存在，创建新用户并保存
            user = createUserWithPhone(phone);
        }
        // 7.保存用户到session
        session.setAttribute("user", BeanUtil.copyProperties(user, UserDTO.class));
        return Result.ok();
    }

    private User createUserWithPhone(String phone) {
        // 1.创建用户
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        // 2.保存用户
        save(user);
        return user;
    }

}
