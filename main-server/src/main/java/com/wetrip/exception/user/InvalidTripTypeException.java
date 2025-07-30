package com.wetrip.exception.user;

import com.wetrip.exception.BaseException;
import com.wetrip.exception.ErrorCode;

public class InvalidTripTypeException extends BaseException {
  public InvalidTripTypeException() {
    super(ErrorCode.INVALID_TRIP_TYPE);
  }

  public InvalidTripTypeException(String detailMessage) {
    super(ErrorCode.INVALID_TRIP_TYPE, detailMessage);
  }
}