package com.huiguli.redisbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author huiguli
 */
@Data
public class RedisData {
    // 过期时间
    private LocalDateTime expireTime;
    private Object data;
}
