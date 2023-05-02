package com.huiguli.redisbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author huiguli
 */
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
