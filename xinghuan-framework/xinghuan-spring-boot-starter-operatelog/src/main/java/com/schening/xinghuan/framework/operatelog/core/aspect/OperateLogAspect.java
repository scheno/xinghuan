package com.schening.xinghuan.framework.operatelog.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;

/**
 *  拦截使用 @OperateLog 注解，如果满足条件，则生成操作日志。
 *  满足如下任一条件，则会进行记录：
 *  1. 使用 @ApiOperation + 非 @GetMapping
 *  2. 使用 @OperateLog 注解
 *  <p>
 *  但是，如果声明 @OperateLog 注解时，将 enable 属性设置为 false 时，强制不记录。
 *
 * @author shenchen
 * @version 1.0
 * @date 2023/2/9 14:46
 */
@Aspect
@Slf4j
public class OperateLogAspect {

}
