package com.wetrip.exception.user;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class GenderMissingException extends BaseException {
  public GenderMissingException() {
    super(ErrorCode.MISSING_GENDER);
  }

  public GenderMissingException(String detailMessage) {
    super(ErrorCode.MISSING_GENDER, detailMessage);
  }
}