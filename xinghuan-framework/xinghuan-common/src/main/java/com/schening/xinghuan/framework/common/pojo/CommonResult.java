package com.schening.xinghuan.framework.common.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * 通用返回
 *
 * @param <T> 数据泛型
 *
 * @author shenchen
 * @version 1.0
 * @date 2023/2/13 14:45
 */
@Data
public class CommonResult<T> implements Serializable {

    /**
     * 错误码
     *
     * @see ErrorCode#getCode()
     */
    private Integer code;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 错误提示，用户可阅读
     *
     * @see ErrorCode#getMsg() ()
     */
    private String msg;

}
