package com.huiguli.redisbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huiguli.redisbackend.dto.Result;
import com.huiguli.redisbackend.entity.Follow;
import com.huiguli.redisbackend.mapper.FollowMapper;
import com.huiguli.redisbackend.service.IFollowService;
import com.huiguli.redisbackend.utils.UserHolder;
import org.springframework.stereotype.Service;

/**
 * @author huiguli
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {
    @Override
    public Result follow(Long followUserId, Boolean isFollow) {
        // 1. 获取登录用户Id
        Long userId = UserHolder.getUser().getId();
        // 2. 判断到底是取关还是要关注
        if (isFollow) {
            // 3. 关注，则新增数据
            Follow follow = new Follow();
            follow.setUserId(userId).setFollowUserId(followUserId);
            save(follow);
        }else {
            // 4. 取关，delete from tb_follow where user_id = ? and follow_user_id = ?
            remove(new QueryWrapper<Follow>().eq("user_id", userId).eq("follow_user_id", followUserId));
        }
        return Result.ok();
    }

    @Override
    public Result isFollow(Long followUserId) {
        // 1. 获取登录用户Id
        Long userId = UserHolder.getUser().getId();
        // 2. 查询是否关注 select count(*) from tb_follow where user_id = ? and follow_user_id = ?
        Integer count = query().eq("user_id", userId).eq("follow_user_id", followUserId).count();
        // 3. 判断
        return Result.ok(count > 0);
    }
}
