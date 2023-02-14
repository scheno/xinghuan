package com.schening.xinghuan.system.dal.dataobject.logger;

import com.baomidou.mybatisplus.annotation.TableName;
import com.schening.xinghuan.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志表
 *
 * @author shenchen
 * @version 1.0
 * @date 2023/2/13 17:27
 */
@TableName(value = "system_operate_log", autoResultMap = true)
//@KeySequence("system_operate_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class OperateLogDO extends BaseDO {

}
