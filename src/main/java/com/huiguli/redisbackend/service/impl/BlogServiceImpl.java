package com.huiguli.redisbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huiguli.redisbackend.dto.Result;
import com.huiguli.redisbackend.dto.UserDTO;
import com.huiguli.redisbackend.entity.Blog;
import com.huiguli.redisbackend.entity.User;
import com.huiguli.redisbackend.mapper.BlogMapper;
import com.huiguli.redisbackend.service.IBlogService;
import com.huiguli.redisbackend.service.IUserService;
import com.huiguli.redisbackend.utils.UserHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author huiguli
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Resource
    private IUserService userService;
    @Override
    public Result queryBlogById(Long id) {
        // 1.查询blog
        Blog blog = getById(id);
        if (blog == null) {
            return Result.fail("笔记不存在！");
        }
        // 2.查询blog有关的用户
        queryBlogUser(blog);
        // 3.查询blog是否被点赞
//        isBlogLiked(blog);
        return Result.ok(blog);
    }

    /*@Override
    public Result saveBlog(Blog blog) {
        // 1.获取登录用户
        UserDTO user = UserHolder.getUser();
        blog.setUserId(user.getId());
        // 2.保存探店笔记
        boolean isSuccess = save(blog);
        if(!isSuccess){
            return Result.fail("新增笔记失败!");
        }
        // 3.查询笔记作者的所有粉丝 select * from tb_follow where follow_user_id = ?
        List<Follow> follows = followService.query().eq("follow_user_id", user.getId()).list();
        // 4.推送笔记id给所有粉丝
        for (Follow follow : follows) {
            // 4.1.获取粉丝id
            Long userId = follow.getUserId();
            // 4.2.推送
            String key = FEED_KEY + userId;
            stringRedisTemplate.opsForZSet().add(key, blog.getId().toString(), System.currentTimeMillis());
        }
        // 5.返回id
        return Result.ok(blog.getId());
    }*/


    private void queryBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }
//    private void isBlogLiked(Blog blog) {
//        // 1.获取登录用户
//        UserDTO user = UserHolder.getUser();
//        if (user == null) {
//            // 用户未登录，无需查询是否点赞
//            return;
//        }
//        Long userId = user.getId();
//        // 2.判断当前登录用户是否已经点赞
//        String key = "blog:liked:" + blog.getId();
//        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
//        blog.setIsLike(score != null);
//    }
}
