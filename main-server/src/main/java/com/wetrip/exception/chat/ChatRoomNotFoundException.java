package com.wetrip.exception.chat;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class ChatRoomNotFoundException extends BaseException {
    public ChatRoomNotFoundException() {
        super(ErrorCode.CHATROOM_NOT_FOUND);
    }

    public ChatRoomNotFoundException(String detailMessage) {
        super(ErrorCode.CHATROOM_NOT_FOUND, detailMessage);
    }
}