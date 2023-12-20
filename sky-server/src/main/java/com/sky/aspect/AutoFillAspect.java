package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类
 */
@Aspect
@Component
@Slf4j

public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知：赋值公共字段
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段填充");
        // 1. 获取mapper方法类型：UPDATE/INSERT
        MethodSignature signature = (MethodSignature)(joinPoint.getSignature());
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 2. 获取mapper方法参数,默认实体对象为第一个
        Object[]args = joinPoint.getArgs();
        if(args == null || args.length == 0)return;
        Object entity = args[0];

        // 3. 找到赋值数据
        LocalDateTime now = LocalDateTime.now();
        Long curUserId = BaseContext.getCurrentId();

        // 4. 赋值
        if(operationType != OperationType.INSERT && operationType != OperationType.UPDATE)
            return;
        if(operationType == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                setCreateUser.invoke(entity,curUserId);
                setCreateTime.invoke(entity,now);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // update和insert都要更新UpdateTime/User
        try {
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
            setUpdateUser.invoke(entity,curUserId);
            setUpdateTime.invoke(entity,now);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
