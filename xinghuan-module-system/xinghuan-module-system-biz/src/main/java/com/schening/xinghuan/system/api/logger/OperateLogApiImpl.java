package com.schening.xinghuan.system.api.logger;

import com.schening.xinghuan.system.api.logger.dto.OperateLogCreateReqDTO;
import com.schening.xinghuan.system.service.logger.OperateLogService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 操作日志 API 实现类
 *
 * @author shenchen
 * @version 1.0
 * @date 2023/2/13 15:57
 */
@Slf4j
@Service
public class OperateLogApiImpl implements OperateLogApi {

    @Resource
    private OperateLogService operateLogService;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        operateLogService.createOperateLog(createReqDTO);
    }

}
