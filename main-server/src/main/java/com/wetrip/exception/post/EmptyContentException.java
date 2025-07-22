package com.wetrip.exception.post;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class EmptyContentException extends BaseException {
    public EmptyContentException() {
        super(ErrorCode.EMPTY_CONTENT);
    }

    public EmptyContentException(String detailMessage) {
        super(ErrorCode.EMPTY_CONTENT, detailMessage);
    }
}