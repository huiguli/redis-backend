package com.huiguli.redisbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huiguli.redisbackend.entity.ShopType;
import com.huiguli.redisbackend.mapper.ShopTypeMapper;
import com.huiguli.redisbackend.service.IShopTypeService;
import org.springframework.stereotype.Service;

/**
 * @author huiguli
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

}
