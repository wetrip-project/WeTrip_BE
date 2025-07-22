package com.wetrip.exception.chat;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class ChatroomExitedException extends BaseException {
    public ChatroomExitedException() {
        super(ErrorCode.CHATROOM_EXITED);
    }

    public ChatroomExitedException(String detailMessage) {
        super(ErrorCode.CHATROOM_EXITED, detailMessage);
    }
}