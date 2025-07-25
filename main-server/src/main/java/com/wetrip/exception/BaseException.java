package com.wetrip.exception;

public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String detailMessage;

    public BaseException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public BaseException(ErrorCode errorCode, String detailMessage) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
}