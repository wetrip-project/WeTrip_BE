package com.wetrip.exception.user;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class ExpiredTokenException extends BaseException {
    public ExpiredTokenException() {
        super(ErrorCode.EXPIRED_TOKEN);
    }

    public ExpiredTokenException(String detailMessage) {
        super(ErrorCode.EXPIRED_TOKEN, detailMessage);
    }
}
