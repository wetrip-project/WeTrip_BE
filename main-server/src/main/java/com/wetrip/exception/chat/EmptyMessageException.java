package com.wetrip.exception.chat;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class EmptyMessageException extends BaseException {
    public EmptyMessageException() {
        super(ErrorCode.EMPTY_MESSAGE);
    }

    public EmptyMessageException(String detailMessage) {
        super(ErrorCode.EMPTY_MESSAGE, detailMessage);
    }
}