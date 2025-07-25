package com.wetrip.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 사용자 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "요청 값이 유효하지 않습니다."),
    ONBOARDING_INCOMPLETE(HttpStatus.BAD_REQUEST, "온보딩 정보가 불완전합니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다. 다시 시도해주세요."),

    // 글(또는 댓글) 관련
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    EMPTY_CONTENT(HttpStatus.BAD_REQUEST, "글 내용은 비어 있을 수 없습니다."),
    UNAUTHORIZED_POST_ACCESS(HttpStatus.FORBIDDEN, "이 글에 대한 권한이 없습니다."),
    ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 글/댓글입니다."),

    // 채팅 관련
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다."),
    EMPTY_MESSAGE(HttpStatus.BAD_REQUEST, "채팅 메시지는 비어 있을 수 없습니다."),
    FORBIDDEN_CHATROOM_ACCESS(HttpStatus.FORBIDDEN, "채팅방에 접근할 수 없습니다."),
    CHATROOM_EXITED(HttpStatus.BAD_REQUEST, "이미 나간 채팅방입니다."),
    CHAT_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅 상대를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
