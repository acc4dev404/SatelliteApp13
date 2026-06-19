package ru.bmstu.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.bmstu.annotation.LogExecutionTime;

import java.lang.reflect.Method;

/**
 * <p> Аспект для логирования времени выполнения методов. </p>
 *
 * <p> Реализует паттерн Decorator через AOP. Перехватывает вызовы методов,
 * помеченных аннотацией {@link LogExecutionTime}, и замеряет время их выполнения. </p>
 */
@Aspect
@Component
public class LoggingAspect {

    /**
     * <p> Замеряет время выполнения метода и выводит результат в консоль. </p>
     *
     * @param joinPoint точка соединения с методом
     * @return результат выполнения метода
     * @throws Throwable если метод выбрасывает исключение
     */
    @Around("@annotation(ru.bmstu.annotation.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.nanoTime();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogExecutionTime annotation = method.getAnnotation(LogExecutionTime.class);

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();

        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.nanoTime();
            long duration = endTime - startTime;

            String unit = annotation.unit();
            double converted = "ns".equals(unit) ? duration : duration / 1_000_000.0;
            String unitDisplay = "ns".equals(unit) ? "нс" : "мс";

            long threshold = annotation.threshold();
            String level = (threshold > 0 && converted > threshold) ? "WARNING" : "INFO";

            System.out.printf("[%s] %s.%s() выполнен за %.2f %s%n",
                    level, className, methodName, converted, unitDisplay);
        }
    }
}