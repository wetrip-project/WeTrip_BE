package com.wetrip.exception.user;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class AgeMissingException extends BaseException {
  public AgeMissingException() {
    super(ErrorCode.MISSING_AGE);
  }

  public AgeMissingException(String detailMessage) {
    super(ErrorCode.MISSING_AGE, detailMessage);
  }
}