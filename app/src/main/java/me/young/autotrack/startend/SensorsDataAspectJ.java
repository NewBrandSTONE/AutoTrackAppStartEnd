package me.young.autotrack.startend;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * 使用AspectJ收集日志
 *
 * @author O.z Young
 * @version 2020-04-19
 */
@Aspect
public class SensorsDataAspectJ {

    private static final String TAG = "SensorsDAtaAspectJ";

    @Around("execution (* *(..))")
    public Object weaveAllMethod(ProceedingJoinPoint pjp) throws Throwable {
        long startNanoTime = System.nanoTime();
        Object returnObj = pjp.proceed();
        long stopNanoTime = System.nanoTime();

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();

        Log.i(TAG, String.format(Locale.CHINA,
                "Method:<%s> cost%s ns"
                , method.toGenericString()
                , String.valueOf(stopNanoTime - startNanoTime)));

        return returnObj;
    }

}
