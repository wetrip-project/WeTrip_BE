package com.wetrip.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int status;
    private String code;
    private String message;
    private String detailMessage;

    public ErrorResponse(int status, String code, String message, String detailMessage) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
            errorCode.getStatus().value(),
            errorCode.name(),
            errorCode.getMessage(),
            null
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, String detailMessage) {
        return new ErrorResponse(
            errorCode.getStatus().value(),
            errorCode.name(),
            errorCode.getMessage(),
            detailMessage
        );
    }
}