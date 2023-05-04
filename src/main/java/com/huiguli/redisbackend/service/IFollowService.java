package com.huiguli.redisbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huiguli.redisbackend.dto.Result;
import com.huiguli.redisbackend.entity.Follow;

/**
 * @author huiguli
 */
public interface IFollowService extends IService<Follow> {
    Result follow(Long followUserId, Boolean isFollow);

    Result isFollow(Long followUserId);
}
