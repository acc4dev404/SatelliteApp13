package ru.bmstu.exception;

/**
 * <p> Исключение, выбрасываемое при ошибках в работе космической системы.</p>
 *
 * <p> Используется для сигнализации о проблемах при создании спутников,
 * неверных параметрах или неподдерживаемых типах. </p>
 */
public class SpaceOperationException extends RuntimeException {

    /**
     * <p> Создает исключение с указанным сообщением. </p>
     *
     * @param message детальное сообщение об ошибке
     */
    public SpaceOperationException(String message) {
        super(message);
    }

    /**
     * <p> Создает исключение с указанным сообщением и причиной. </p>
     *
     * @param message детальное сообщение об ошибке
     * @param cause причина исключения
     */
    public SpaceOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}