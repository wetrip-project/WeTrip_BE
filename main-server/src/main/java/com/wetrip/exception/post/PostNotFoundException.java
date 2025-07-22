package com.wetrip.exception.post;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class PostNotFoundException extends BaseException {
    public PostNotFoundException() {
        super(ErrorCode.POST_NOT_FOUND);
    }

    public PostNotFoundException(String detailMessage) {
        super(ErrorCode.POST_NOT_FOUND, detailMessage);
    }
}