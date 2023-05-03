package com.huiguli.redisbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huiguli.redisbackend.dto.Result;
import com.huiguli.redisbackend.entity.Blog;

/**
 * @author huiguli
 */
public interface IBlogService extends IService<Blog> {
    Result queryBlogById(Long id);

    Result queryHotBlog(Integer current);

    Result likeBlog(Long id);
}
