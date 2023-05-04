package com.huiguli.redisbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huiguli.redisbackend.dto.Result;
import com.huiguli.redisbackend.dto.UserDTO;
import com.huiguli.redisbackend.entity.Follow;
import com.huiguli.redisbackend.mapper.FollowMapper;
import com.huiguli.redisbackend.service.IFollowService;
import com.huiguli.redisbackend.service.IUserService;
import com.huiguli.redisbackend.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huiguli
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private IUserService userService;
    @Override
    public Result follow(Long followUserId, Boolean isFollow) {
        // 1. 获取登录用户Id
        Long userId = UserHolder.getUser().getId();
        String key = "follows:" + userId;
        // 2. 判断到底是取关还是要关注
        if (isFollow) {
            // 3. 关注，则新增数据
            Follow follow = new Follow();
            follow.setUserId(userId).setFollowUserId(followUserId);
            boolean isSuccess = save(follow);
            if (isSuccess) {
                // 把关注的用户id,放入redis的set集合中 sadd userId followUserId
                stringRedisTemplate.opsForSet().add(key, followUserId.toString());
            }
        }else {
            // 4. 取关，delete from tb_follow where user_id = ? and follow_user_id = ?
            boolean isSuccess = remove(new QueryWrapper<Follow>()
                    .eq("user_id", userId).eq("follow_user_id", followUserId));
            if (isSuccess) {
                // 把关注的用户id，从redis中移除
                stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
            }
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

    @Override
    public Result followCommons(Long id) {
        // 1. 获取登录用户Id
        Long userId = UserHolder.getUser().getId();
        // 2. 在redis中求交集
        String key = "follows:" + userId;
        String key2 = "follows:" + id;
        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(key, key2);
        if (intersect == null || intersect.isEmpty()) {
            // 无交集返回一个空集
            return Result.ok(Collections.emptyList());
        }
        // 3. 解析 id 集合
        List<Long> ids = intersect.stream().map(Long::valueOf).collect(Collectors.toList());
        // 4. 根据 id 查询用户
        userService.listByIds(ids)
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.ok();
    }
}
