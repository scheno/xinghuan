package com.schening.xinghuan.framework.operatelog.core.aspect;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.google.common.collect.Maps;
import com.schening.xinghuan.framework.common.pojo.CommonResult;
import com.schening.xinghuan.framework.common.util.json.JsonUtils;
import com.schening.xinghuan.framework.common.util.monitor.TracerUtils;
import com.schening.xinghuan.framework.common.util.servlet.ServletUtils;
import com.schening.xinghuan.framework.operatelog.core.enums.OperateTypeEnum;
import com.schening.xinghuan.framework.operatelog.core.service.OperateLog;
import com.schening.xinghuan.framework.operatelog.core.service.OperateLogFrameworkService;
import com.schening.xinghuan.framework.web.util.WebFrameworkUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import static com.schening.xinghuan.framework.common.exception.enums.GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR;
import static com.schening.xinghuan.framework.common.exception.enums.GlobalErrorCodeConstants.SUCCESS;

/**
 * ???????????? @OperateLog ?????????????????????????????????????????????????????? ???????????????????????????????????????????????? 1. ?????? @ApiOperation + ??? @GetMapping 2. ??????
 *
 * @author shenchen
 * @version 1.0
 * @OperateLog ??????
 * <p>
 * ????????????????????? @OperateLog ??????????????? enable ??????????????? false ????????????????????????
 * @date 2023/2/9 14:46
 */
@Aspect
@Slf4j
public class OperateLogAspect {

    @Resource
    private OperateLogFrameworkService operateLogFrameworkService;

    /**
     * ????????????????????????????????????
     *
     * @see OperateLog#getContent()
     */
    private static final ThreadLocal<String> CONTENT = new ThreadLocal<>();

    /**
     * ????????????????????????????????????
     *
     * @see OperateLog#getExts()
     */
    private static final ThreadLocal<Map<String, Object>> EXTS = new ThreadLocal<>();

    @Around("!@annotation(io.swagger.v3.oas.annotations.Operation) && @annotation(operateLog)")
    public Object around(ProceedingJoinPoint joinPoint, com.schening.xinghuan.framework.operatelog.core.annotations.OperateLog operateLog) throws Throwable {
        // ???????????????????????? @OperateLog ???????????????
        return around0(joinPoint, operateLog, null);
    }

    private Object around0(ProceedingJoinPoint joinPoint, com.schening.xinghuan.framework.operatelog.core.annotations.OperateLog operateLog,
            Operation operation) throws Throwable {
        LocalDateTime startTime = LocalDateTime.now();
        try {
            // ??????????????????
            Object result = joinPoint.proceed();
            // ????????????????????????????????????
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
            // ????????????????????????
            if (!isLogEnable(joinPoint, operateLog)) {
                return;
            }
            // ????????????????????????
            this.log0(joinPoint, operateLog, operation, startTime, result, exception);
        } catch (Throwable ex) {
            log.error("[log][?????????????????????????????????????????????????????? joinPoint({}) operateLog({}) apiOperation({}) result({}) exception({}) ]",
                    joinPoint, operateLog, operation, result, exception, ex);
        }
    }

    private void log0(ProceedingJoinPoint joinPoint,
            com.schening.xinghuan.framework.operatelog.core.annotations.OperateLog operateLog,
            Operation operation,
            LocalDateTime startTime, Object result, Throwable exception) {
        OperateLog operateLogObj = new com.schening.xinghuan.framework.operatelog.core.service.OperateLog();
        // ??????????????????
        operateLogObj.setTraceId(TracerUtils.getTraceId());
        operateLogObj.setStartTime(startTime);
        // ??????????????????
        fillUserFields(operateLogObj);
        // ??????????????????
        fillModuleFields(operateLogObj, joinPoint, operateLog, operation);
        // ??????????????????
        fillRequestFields(operateLogObj);
        // ??????????????????
        fillMethodFields(operateLogObj, joinPoint, operateLog, startTime, result, exception);

        // ??????????????????
        operateLogFrameworkService.createOperateLog(operateLogObj);
    }

    private static void fillUserFields(OperateLog operateLogObj) {
        operateLogObj.setUserId(WebFrameworkUtils.getLoginUserId());
        operateLogObj.setUserType(WebFrameworkUtils.getLoginUserType());
    }

    private static void fillModuleFields(OperateLog operateLogObj,
            ProceedingJoinPoint joinPoint, com.schening.xinghuan.framework.operatelog.core.annotations.OperateLog operateLog, Operation operation) {
        // module ??????
        if (operateLog != null) {
            operateLogObj.setModule(operateLog.module());
        }
        if (StrUtil.isEmpty(operateLogObj.getModule())) {
            Tag tag = getClassAnnotation(joinPoint, Tag.class);
            if (tag != null) {
                // ???????????? @Tag ??? name ??????
                if (StrUtil.isNotEmpty(tag.name())) {
                    operateLogObj.setModule(tag.name());
                }
                // ????????????????????? @API ??? description ??????
                if (StrUtil.isEmpty(operateLogObj.getModule()) && ArrayUtil.isNotEmpty(tag.description())) {
                    operateLogObj.setModule(tag.description());
                }
            }
        }
        // name ??????
        if (operateLog != null) {
            operateLogObj.setName(operateLog.name());
        }
        if (StrUtil.isEmpty(operateLogObj.getName()) && operation != null) {
            operateLogObj.setName(operation.summary());
        }
        // type ??????
        if (operateLog != null && ArrayUtil.isNotEmpty(operateLog.type())) {
            operateLogObj.setType(operateLog.type()[0].getType());
        }
        if (operateLogObj.getType() == null) {
            RequestMethod requestMethod = obtainFirstMatchRequestMethod(obtainRequestMethod(joinPoint));
            OperateTypeEnum operateLogType = convertOperateLogType(requestMethod);
            operateLogObj.setType(operateLogType != null ? operateLogType.getType() : null);
        }
        // content ??? exts ??????
        operateLogObj.setContent(CONTENT.get());
        operateLogObj.setExts(EXTS.get());
    }

    private static void fillRequestFields(OperateLog operateLogObj) {
        // ?????? Request ??????
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return;
        }
        // ??????????????????
        operateLogObj.setRequestMethod(request.getMethod());
        operateLogObj.setRequestUrl(request.getRequestURI());
        operateLogObj.setUserIp(ServletUtil.getClientIP(request));
        operateLogObj.setUserAgent(ServletUtils.getUserAgent(request));
    }

    private static void fillMethodFields(OperateLog operateLogObj,
            ProceedingJoinPoint joinPoint, com.schening.xinghuan.framework.operatelog.core.annotations.OperateLog operateLog,
            LocalDateTime startTime, Object result, Throwable exception) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        operateLogObj.setJavaMethod(methodSignature.toString());
        if (operateLog == null || operateLog.logArgs()) {
            operateLogObj.setJavaMethodArgs(obtainMethodArgs(joinPoint));
        }
        if (operateLog == null || operateLog.logResultData()) {
            operateLogObj.setResultData(obtainResultData(result));
        }
        operateLogObj.setDuration((int) (LocalDateTimeUtil.between(startTime, LocalDateTime.now()).toMillis()));
        // ?????????????????? resultCode ??? resultMsg ??????
        if (result instanceof CommonResult) {
            CommonResult<?> commonResult = (CommonResult<?>) result;
            operateLogObj.setResultCode(commonResult.getCode());
            operateLogObj.setResultMsg(commonResult.getMsg());
        } else {
            operateLogObj.setResultCode(SUCCESS.getCode());
        }
        // ?????????????????? resultCode ??? resultMsg ??????
        if (exception != null) {
            operateLogObj.setResultCode(INTERNAL_SERVER_ERROR.getCode());
            operateLogObj.setResultMsg(ExceptionUtil.getRootCauseMessage(exception));
        }
    }

    private static boolean isLogEnable(ProceedingJoinPoint joinPoint, com.schening.xinghuan.framework.operatelog.core.annotations.OperateLog operateLog) {
        // ??? @OperateLog ??????????????????
        if (operateLog != null) {
            return operateLog.enable();
        }
        // ?????? @ApiOperation ?????????????????????????????? POST???PUT???DELETE ?????????
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

    private static RequestMethod obtainFirstMatchRequestMethod(RequestMethod[] requestMethods) {
        if (ArrayUtil.isEmpty(requestMethods)) {
            return null;
        }
        // ???????????????????????? POST???PUT???DELETE
        RequestMethod result = obtainFirstLogRequestMethod(requestMethods);
        if (result != null) {
            return result;
        }
        // ???????????????????????? GET
        result = Arrays.stream(requestMethods).filter(requestMethod -> requestMethod == RequestMethod.GET)
                .findFirst().orElse(null);
        if (result != null) {
            return result;
        }
        // ????????????????????????
        return requestMethods[0];
    }

    private static OperateTypeEnum convertOperateLogType(RequestMethod requestMethod) {
        if (requestMethod == null) {
            return null;
        }
        switch (requestMethod) {
            case GET:
                return OperateTypeEnum.GET;
            case POST:
                return OperateTypeEnum.CREATE;
            case PUT:
                return OperateTypeEnum.UPDATE;
            case DELETE:
                return OperateTypeEnum.DELETE;
            default:
                return OperateTypeEnum.OTHER;
        }
    }

    private static RequestMethod[] obtainRequestMethod(ProceedingJoinPoint joinPoint) {
        RequestMapping requestMapping = AnnotationUtils.getAnnotation( // ?????? Spring ??????????????????????????? @RequestMapping ????????????
                ((MethodSignature) joinPoint.getSignature()).getMethod(), RequestMapping.class);
        return requestMapping != null ? requestMapping.method() : new RequestMethod[]{};
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Annotation> T getMethodAnnotation(ProceedingJoinPoint joinPoint, Class<T> annotationClass) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(annotationClass);
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Annotation> T getClassAnnotation(ProceedingJoinPoint joinPoint, Class<T> annotationClass) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getDeclaringClass().getAnnotation(annotationClass);
    }

    private static String obtainMethodArgs(ProceedingJoinPoint joinPoint) {
        // TODO ??????????????????????????????
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] argNames = methodSignature.getParameterNames();
        Object[] argValues = joinPoint.getArgs();
        // ????????????
        Map<String, Object> args = Maps.newHashMapWithExpectedSize(argValues.length);
        for (int i = 0; i < argNames.length; i++) {
            String argName = argNames[i];
            Object argValue = argValues[i];
            // ???????????????????????? ignore ????????????????????? null ????????????
            args.put(argName, !isIgnoreArgs(argValue) ? argValue : "[ignore]");
        }
        return JsonUtils.toJsonString(args);
    }

    private static String obtainResultData(Object result) {
        // TODO ??????????????????????????????
        if (result instanceof CommonResult) {
            result = ((CommonResult<?>) result).getData();
        }
        return JsonUtils.toJsonString(result);
    }

    private static boolean isIgnoreArgs(Object object) {
        Class<?> clazz = object.getClass();
        // ?????????????????????
        if (clazz.isArray()) {
            return IntStream.range(0, Array.getLength(object))
                    .anyMatch(index -> isIgnoreArgs(Array.get(object, index)));
        }
        // ????????????????????????Collection???Map ?????????
        if (Collection.class.isAssignableFrom(clazz)) {
            return ((Collection<?>) object).stream()
                    .anyMatch((Predicate<Object>) OperateLogAspect::isIgnoreArgs);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return isIgnoreArgs(((Map<?, ?>) object).values());
        }
        // obj
        return object instanceof MultipartFile
                || object instanceof HttpServletRequest
                || object instanceof HttpServletResponse
                || object instanceof BindingResult;
    }

}
