package com.huiguli.redisbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huiguli.redisbackend.dto.Result;
import com.huiguli.redisbackend.entity.Voucher;

/**
 * @author huiguli
 */
public interface IVoucherService extends IService<Voucher> {
    Result queryVoucherOfShop(Long shopId);
}