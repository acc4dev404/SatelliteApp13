package ru.bmstu.entity;

public enum OutboxStatus {
    /** Ожидает отправки */
    PENDING,

    /** Успешно отправлено */
    SENT,

    /** Ошибка при отправке */
    FAILED
}