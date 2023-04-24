package com.huiguli.redisbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huiguli.redisbackend.dto.Result;
import com.huiguli.redisbackend.entity.Shop;
import com.huiguli.redisbackend.mapper.ShopMapper;
import com.huiguli.redisbackend.service.IShopService;
import org.springframework.stereotype.Service;

/**
 * @author huiguli
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

//    @Resource
//    private StringRedis
    @Override
    public Result queryShopById(Long id) {
        // 1.从redis中查询商铺缓存

        // 2.判断是否存在

        // 3.存在直接返回

        // 4.不存在根据id,在数据库中查

        // 5.不存在返回错误

        // 6.存在，写入 redis

        // 7.返回
        return null;
    }
}
