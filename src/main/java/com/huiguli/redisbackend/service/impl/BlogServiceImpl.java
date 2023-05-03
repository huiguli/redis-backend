package com.huiguli.redisbackend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huiguli.redisbackend.constant.SystemConstants;
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

    @Override
    public Result queryHotBlog(Integer current) {
        // 根据用户查询
        Page<Blog> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        // 查询用户
        records.forEach(this::queryBlogUser);
        return Result.ok(records);
    }

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
