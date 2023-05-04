package com.huiguli.redisbackend.controller;

import com.huiguli.redisbackend.dto.Result;
import com.huiguli.redisbackend.service.IFollowService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author huiguli
 */
@RestController
@RequestMapping("/follow")
public class FollowController {
    @Resource
    private IFollowService followService;
    /*
    * 是否关注
    */
    @PutMapping("{id}/{isFollow}")
    public Result follow(@PathVariable("id") Long followUserId, @PathVariable("isFollow") Boolean isFollow) {
        return followService.follow(followUserId, isFollow);
    }

    @GetMapping("or/not/{id}")
    public Result isFollow(@PathVariable("id") Long followUserId) {
        return followService.isFollow(followUserId);
    }

    @GetMapping("common/{id}")
    public Result followCommons(@PathVariable("id") Long id) {
        // 当前用户id 与 此博主id 的共同关注
        return followService.followCommons(id);
    }
}
