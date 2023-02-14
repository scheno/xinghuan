package com.schening.xinghuan.system.convert.logger;


import com.schening.xinghuan.system.api.logger.dto.OperateLogCreateReqDTO;
import com.schening.xinghuan.system.dal.dataobject.logger.OperateLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/2/14 10:35
 */
@Mapper
public interface OperateLogConvert {

    OperateLogConvert INSTANCE = Mappers.getMapper(OperateLogConvert.class);

    OperateLogDO convert(OperateLogCreateReqDTO operateLogCreateReq);

}
