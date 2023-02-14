package com.schening.xinghuan.system.service.logger.impl;

import cn.hutool.db.PageResult;
import com.schening.xinghuan.system.api.logger.dto.OperateLogCreateReqDTO;
import com.schening.xinghuan.system.controller.admin.logger.vo.OperateLogExportReqVO;
import com.schening.xinghuan.system.controller.admin.logger.vo.OperateLogPageReqVO;
import com.schening.xinghuan.system.convert.logger.OperateLogConvert;
import com.schening.xinghuan.system.dal.dataobject.logger.OperateLogDO;
import com.schening.xinghuan.system.dal.mysql.logger.OperateLogMapper;
import com.schening.xinghuan.system.service.logger.OperateLogService;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/2/14 10:14
 */
@Slf4j
@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Resource
    private OperateLogMapper operateLogMapper;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        OperateLogDO operateLog = OperateLogConvert.INSTANCE.convert(createReqDTO);
        operateLogMapper.insert(operateLog);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO reqVO) {
        return null;
    }

    @Override
    public List<OperateLogDO> getOperateLogList(OperateLogExportReqVO reqVO) {
        return null;
    }
}
