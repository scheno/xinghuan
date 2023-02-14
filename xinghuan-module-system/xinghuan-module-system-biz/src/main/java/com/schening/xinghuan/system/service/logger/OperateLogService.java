package com.schening.xinghuan.system.service.logger;

import cn.hutool.db.PageResult;
import com.schening.xinghuan.system.api.logger.dto.OperateLogCreateReqDTO;
import com.schening.xinghuan.system.controller.admin.logger.vo.OperateLogExportReqVO;
import com.schening.xinghuan.system.controller.admin.logger.vo.OperateLogPageReqVO;
import com.schening.xinghuan.system.dal.dataobject.logger.OperateLogDO;
import java.util.List;

/**
 * 操作日志 Service 接口
 *
 * @author shenchen
 * @version 1.0
 * @date 2023/2/13 15:59
 */
public interface OperateLogService {

    /**
     * 记录操作日志
     *
     * @param createReqDTO 操作日志请求
     */
    void createOperateLog(OperateLogCreateReqDTO createReqDTO);

    /**
     * 获得操作日志分页列表
     *
     * @param reqVO 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO reqVO);

    /**
     * 获得操作日志列表
     *
     * @param reqVO 列表条件
     * @return 日志列表
     */
    List<OperateLogDO> getOperateLogList(OperateLogExportReqVO reqVO);

}
