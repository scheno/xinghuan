package com.schening.xinghuan.shop.common.entity;

import java.io.Serializable;

/**
 * 结果实体类
 *
 * @author shenchen
 * @version 1.0
 * @date 2023/3/21 10:50
 */
public class Result implements Serializable {
    private Boolean success;
    private String message;

    public Result() {
    }

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
