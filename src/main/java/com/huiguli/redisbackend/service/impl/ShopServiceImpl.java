package com.huiguli.redisbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huiguli.redisbackend.entity.Shop;
import com.huiguli.redisbackend.mapper.ShopMapper;
import com.huiguli.redisbackend.service.IShopService;
import org.springframework.stereotype.Service;

/**
 * @author huiguli
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
}
