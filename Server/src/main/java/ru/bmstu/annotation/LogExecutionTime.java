package ru.bmstu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> Аннотация для логирования времени выполнения метода. </p>
 *
 * <p> Методы, помеченные этой аннотацией, будут автоматически логировать
 * время своего выполнения с помощью Aspect-Oriented Programming. </p>
 *
 * <p> Пример использования: </p>
 * <pre>{@code
 * @LogExecutionTime
 * public void someMethod() {
 *     // код метода
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {

    /**
     * <p> Единица измерения времени выполнения. </p>
     *
     * @return "ms" для миллисекунд, "ns" для наносекунд
     */
    String unit() default "ms";

    /**
     * <p> Пороговое значение в миллисекундах для логирования предупреждения. </p>
     *
     * @return пороговое значение
     */
    long threshold() default -1;
}