package com.solux.sm137.infra.apiPayload.status;

import com.solux.sm137.infra.apiPayload.base.BaseCode;
import com.solux.sm137.infra.apiPayload.base.ResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FailureStatus implements BaseCode {
    _BAD_REQUEST(400, "잘못된 요청입니다."),
    _USER_NOT_FOUND(400, "존재하지 않는 회원입니다."),
    _UNAUTHORIZED(401, "인증되지 않은 사용자입니다."),
    _NOT_FOUND(404, "요청한 자원을 찾을 수 없습니다."),
    _INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    _COMPLAINT_NOT_FOUND(400, "존재하지 않는 글입니다."),
    _ANSWER_REGISTRATION_FAILED(400, "답변 등록에 실패했습니다."),
    _AUTHENTICATION_FAILED(400, "OAuth2User가 null로, 사용자 인증에 실패했습니다."),
    _CONFLICT(409, "대기 중인 민원만 수정 가능합니다."),
    _ALREADY_LIKED_COMMENT (409, "이미 좋아요를 누르셨습니다."),
    _ALREADY_LIKED_COMPLAINT(409, "이미 좋아요를 누르셨습니다.");

    private final int code;
    private final String message;

    @Override
    public ResponseDTO getResponse() {
        return ResponseDTO.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static FailureStatus getByCode(int code) {
        for (FailureStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null; // 코드에 해당하는 상태가 없으면 null 반환
    }

}
