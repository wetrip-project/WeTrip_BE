package com.wetrip.exception.chat;

public class ChatRoomNotFoundException extends RuntimeException {

    public ChatRoomNotFoundException(String detailMessage) {
        super(detailMessage);
    }
}
