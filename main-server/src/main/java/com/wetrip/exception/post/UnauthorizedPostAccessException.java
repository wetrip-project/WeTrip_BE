package com.wetrip.exception.post;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class UnauthorizedPostAccessException extends BaseException {
    public UnauthorizedPostAccessException() {
        super(ErrorCode.UNAUTHORIZED_POST_ACCESS);
    }

    public UnauthorizedPostAccessException(String detailMessage) {
        super(ErrorCode.UNAUTHORIZED_POST_ACCESS, detailMessage);
    }
}