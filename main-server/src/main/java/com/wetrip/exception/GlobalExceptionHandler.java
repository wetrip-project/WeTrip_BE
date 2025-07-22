package com.wetrip.exception;

import com.wetrip.exception.chat.ChatRoomNotFoundException;
import com.wetrip.exception.chat.ChatUserNotFoundException;
import com.wetrip.exception.chat.ChatroomExitedException;
import com.wetrip.exception.chat.EmptyMessageException;
import com.wetrip.exception.chat.ForbiddenChatroomAccessException;
import com.wetrip.exception.post.AlreadyDeletedException;
import com.wetrip.exception.post.EmptyContentException;
import com.wetrip.exception.post.PostNotFoundException;
import com.wetrip.exception.post.UnauthorizedPostAccessException;
import com.wetrip.exception.user.*;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // user 관련
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        return buildResponse(ErrorCode.USER_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateNickname(DuplicateNicknameException e) {
        return buildResponse(ErrorCode.DUPLICATE_NICKNAME, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return buildResponse(ErrorCode.INVALID_ARGUMENT, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return buildResponse(ErrorCode.INVALID_ARGUMENT, e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
        return buildResponse(ErrorCode.INVALID_ARGUMENT, e.getMessage());
    }


    //post 관련
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFound(PostNotFoundException e) {
        return buildResponse(ErrorCode.POST_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(EmptyContentException.class)
    public ResponseEntity<ErrorResponse> handleEmptyPostContent(EmptyContentException e) {
        return buildResponse(ErrorCode.EMPTY_CONTENT, e.getMessage());
    }

    @ExceptionHandler(UnauthorizedPostAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedPost(UnauthorizedPostAccessException e) {
        return buildResponse(ErrorCode.UNAUTHORIZED_POST_ACCESS, e.getMessage());
    }

    @ExceptionHandler(AlreadyDeletedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyDeletedPost(AlreadyDeletedException e) {
        return buildResponse(ErrorCode.ALREADY_DELETED, e.getMessage());
    }

    // chat 관련
    @ExceptionHandler(ChatRoomNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChatRoomNotFound(ChatRoomNotFoundException e) {
        return buildResponse(ErrorCode.CHATROOM_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(EmptyMessageException.class)
    public ResponseEntity<ErrorResponse> handleEmptyMessage(EmptyMessageException e) {
        return buildResponse(ErrorCode.EMPTY_MESSAGE, e.getMessage());
    }

    @ExceptionHandler(ForbiddenChatroomAccessException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenChatroom(ForbiddenChatroomAccessException e) {
        return buildResponse(ErrorCode.FORBIDDEN_CHATROOM_ACCESS, e.getMessage());
    }

    @ExceptionHandler(ChatroomExitedException.class)
    public ResponseEntity<ErrorResponse> handleChatroomExited(ChatroomExitedException e) {
        return buildResponse(ErrorCode.CHATROOM_EXITED, e.getMessage());
    }

    @ExceptionHandler(ChatUserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChatUserNotFound(ChatUserNotFoundException e) {
        return buildResponse(ErrorCode.CHAT_USER_NOT_FOUND, e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode errorCode, String detailMessage) {
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ErrorResponse.of(errorCode, detailMessage));
    }
}
