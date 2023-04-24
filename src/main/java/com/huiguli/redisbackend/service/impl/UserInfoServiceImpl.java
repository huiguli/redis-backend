package com.huiguli.redisbackend.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huiguli.redisbackend.entity.UserInfo;
import com.huiguli.redisbackend.mapper.UserInfoMapper;
import com.huiguli.redisbackend.service.IUserInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author huiguli
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
