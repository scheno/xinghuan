package com.schening.xinghuan.shop.common.exception;

import com.schening.xinghuan.shop.common.constant.ShopCode;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 16:49
 */
public class CastException {

    public static void cast(ShopCode shopCode) {
        throw new CustomerException(shopCode);
    }

}
