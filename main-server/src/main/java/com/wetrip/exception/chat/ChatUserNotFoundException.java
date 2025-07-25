package com.wetrip.exception.chat;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class ChatUserNotFoundException extends BaseException {
    public ChatUserNotFoundException() {
        super(ErrorCode.CHAT_USER_NOT_FOUND);
    }

    public ChatUserNotFoundException(String detailMessage) {
        super(ErrorCode.CHAT_USER_NOT_FOUND, detailMessage);
    }
}