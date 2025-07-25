package com.wetrip.exception.post;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class AlreadyDeletedException extends BaseException {
    public AlreadyDeletedException() {
        super(ErrorCode.ALREADY_DELETED);
    }

    public AlreadyDeletedException(String detailMessage) {
        super(ErrorCode.ALREADY_DELETED, detailMessage);
    }
}