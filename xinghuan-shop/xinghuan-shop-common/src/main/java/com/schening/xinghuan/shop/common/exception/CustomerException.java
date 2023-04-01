package com.schening.xinghuan.shop.common.exception;

import com.schening.xinghuan.shop.common.constant.ShopCode;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 16:50
 */
public class CustomerException extends RuntimeException{

    private final ShopCode shopCode;

    public CustomerException(ShopCode shopCode) {
        this.shopCode = shopCode;
    }

}
