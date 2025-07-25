package com.wetrip.exception.chat;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class ForbiddenChatroomAccessException extends BaseException {
  public ForbiddenChatroomAccessException() {
    super(ErrorCode.FORBIDDEN_CHATROOM_ACCESS);
  }

  public ForbiddenChatroomAccessException(String detailMessage) {
    super(ErrorCode.FORBIDDEN_CHATROOM_ACCESS, detailMessage);
  }
}