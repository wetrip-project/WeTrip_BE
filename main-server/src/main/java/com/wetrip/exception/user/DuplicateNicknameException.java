package com.wetrip.exception.user;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class DuplicateNicknameException extends BaseException {
    public DuplicateNicknameException() {
        super(ErrorCode.DUPLICATE_NICKNAME);
    }

    public DuplicateNicknameException(String detailMessage) {
        super(ErrorCode.DUPLICATE_NICKNAME, detailMessage);
    }
}
