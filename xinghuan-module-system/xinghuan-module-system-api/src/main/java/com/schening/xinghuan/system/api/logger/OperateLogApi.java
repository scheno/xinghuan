package com.schening.xinghuan.system.api.logger;

import com.schening.xinghuan.system.api.logger.dto.OperateLogCreateReqDTO;
import javax.validation.Valid;

/**
 * 操作日志 API 接口
 *
 * @author shenchen
 * @version 1.0
 * @date 2023/2/13 15:48
 */
public interface OperateLogApi {

    /**
     * 创建操作日志
     *
     * @param createReqDTO 请求
     */
    void createOperateLog(@Valid OperateLogCreateReqDTO createReqDTO);

}
