package com.huiguli.redisbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huiguli.redisbackend.dto.Result;
import com.huiguli.redisbackend.entity.Shop;

/**
 * @author huiguli
 */
public interface IShopService extends IService<Shop> {
     Result queryById(Long id);

     Result update(Shop shop);
}
