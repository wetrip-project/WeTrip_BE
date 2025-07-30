package com.wetrip.exception.user;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class InvalidTokenException extends BaseException {
  public InvalidTokenException() {
    super(ErrorCode.INVALID_TOKEN);
  }

  public InvalidTokenException(String detailMessage) {
    super(ErrorCode.INVALID_TOKEN, detailMessage);
  }
}
