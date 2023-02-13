package com.schening.xinghuan.framework.operatelog.core.aspect;

import cn.hutool.core.util.ArrayUtil;
import com.schening.xinghuan.framework.common.util.monitor.TracerUtils;
import com.schening.xinghuan.framework.operatelog.core.service.OperateLog;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 拦截使用 @OperateLog 注解，如果满足条件，则生成操作日志。 满足如下任一条件，则会进行记录： 1. 使用 @ApiOperation + 非 @GetMapping 2. 使用
 *
 * @author shenchen
 * @version 1.0
 * @OperateLog 注解
 * <p>
 * 但是，如果声明 @OperateLog 注解时，将 enable 属性设置为 false 时，强制不记录。
 * @date 2023/2/9 14:46
 */
@Aspect
@Slf4j
public class OperateLogAspect {

    @Around("!@annotation(io.swagger.v3.oas.annotations.Operation) && @annotation(operateLog)")
    public Object around(ProceedingJoinPoint joinPoint, com.schening.xinghuan.framework.operatelog.core.annotations.OperateLog operateLog) throws Throwable {
        // 兼容处理，只添加 @OperateLog 注解的情况
        return around0(joinPoint, operateLog, null);
    }

    private Object around0(ProceedingJoinPoint joinPoint, com.schening.xinghuan.framework.operatelog.core.annotations.OperateLog operateLog,
            Operation operation) throws Throwable {
        LocalDateTime startTime = LocalDateTime.now();
        try {
            // 执行原有逻辑
            Object result = joinPoint.proceed();
            // 记录正常执行时的操作日志
            this.log(joinPoint, operateLog, operation, startTime, result, null);
            return result;
        } catch (Exception e) {

        }
        return null;
    }

    private void log(ProceedingJoinPoint joinPoint,
            com.schening.xinghuan.framework.operatelog.core.annotations.OperateLog operateLog,
            Operation operation,
            LocalDateTime startTime, Object result, Throwable exception) {
        try {
            // 判断不记录的情况
            if (!isLogEnable(joinPoint, operateLog)) {
                return;
            }
            // 真正记录操作日志
            this.log0(joinPoint, operateLog, operation, startTime, result, exception);
        } catch (Throwable ex) {
            log.error("[log][记录操作日志时，发生异常，其中参数是 joinPoint({}) operateLog({}) apiOperation({}) result({}) exception({}) ]",
                    joinPoint, operateLog, operation, result, exception, ex);
        }
    }

    private void log0(ProceedingJoinPoint joinPoint,
            com.schening.xinghuan.framework.operatelog.core.annotations.OperateLog operateLog,
            Operation operation,
            LocalDateTime startTime, Object result, Throwable exception) {
        OperateLog operateLogObj = new com.schening.xinghuan.framework.operatelog.core.service.OperateLog();
        // 补全通用字段
        operateLogObj.setTraceId(TracerUtils.getTraceId());
        operateLogObj.setStartTime(startTime);
        // 补充用户信息
        fillUserFields(operateLogObj);
        // 补全模块信息
//        fillModuleFields(operateLogObj, joinPoint, operateLog, operation);
        // 补全请求信息
        fillRequestFields(operateLogObj);
        // 补全方法信息
//        fillMethodFields(operateLogObj, joinPoint, operateLog, startTime, result, exception);

        // 异步记录日志
//        operateLogFrameworkService.createOperateLog(operateLogObj);
    }

    private static void fillUserFields(OperateLog operateLogObj) {

    }

    private static void fillModuleFields(OperateLog operateLogObj) {

    }

    private static void fillRequestFields(OperateLog operateLogObj) {

    }

    private static void fillMethodFields(OperateLog operateLogObj) {

    }

    private static boolean isLogEnable(ProceedingJoinPoint joinPoint, com.schening.xinghuan.framework.operatelog.core.annotations.OperateLog operateLog) {
        // 有 @OperateLog 注解的情况下
        if (operateLog != null) {
            return operateLog.enable();
        }
        // 没有 @ApiOperation 注解的情况下，只记录 POST、PUT、DELETE 的情况
        return obtainFirstLogRequestMethod(obtainRequestMethod(joinPoint)) != null;
    }

    private static RequestMethod obtainFirstLogRequestMethod(RequestMethod[] requestMethods) {
        if (ArrayUtil.isEmpty(requestMethods)) {
            return null;
        }
        return Arrays.stream(requestMethods).filter(requestMethod ->
                        requestMethod == RequestMethod.POST
                                || requestMethod == RequestMethod.PUT
                                || requestMethod == RequestMethod.DELETE)
                .findFirst().orElse(null);
    }

    private static RequestMethod[] obtainRequestMethod(ProceedingJoinPoint joinPoint) {
        RequestMapping requestMapping = AnnotationUtils.getAnnotation( // 使用 Spring 的工具类，可以处理 @RequestMapping 别名注解
                ((MethodSignature) joinPoint.getSignature()).getMethod(), RequestMapping.class);
        return requestMapping != null ? requestMapping.method() : new RequestMethod[]{};
    }

}
