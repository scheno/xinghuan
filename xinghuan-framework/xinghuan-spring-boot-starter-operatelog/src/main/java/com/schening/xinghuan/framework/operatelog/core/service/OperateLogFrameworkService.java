package com.schening.xinghuan.framework.operatelog.core.service;

/**
 * 操作日志 Framework Service 接口
 *
 * @author shenchen
 * @version 1.0
 * @date 2023/2/13 15:37
 */
public interface OperateLogFrameworkService {

    /**
     * 记录操作日志
     *
     * @param operateLog 操作日志请求
     */
    void createOperateLog(OperateLog operateLog);

}
