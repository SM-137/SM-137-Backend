package com.solux.sm137.infra.apiPayload.status;

import com.solux.sm137.infra.apiPayload.base.BaseCode;
import com.solux.sm137.infra.apiPayload.base.ResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    _OK(200, "요청에 성공했습니다."),
    _MODIFY_USER_INFO_SUCCESS(200, "개인정보수정에 성공하였습니다."),
    _GET_USER_INFO_SUCCESS(200, "회원정보 조회에 성공하였습니다."),
    _GET_MY_COMPLAINTS_SUCCESS(200, "내 민원 리스트 조회에 성공하였습니다."),
    _GET_RESULTS_SUCCESS(200, "결과 조회에 성공하였습니다."),
    _GET_MANAGER_COMPLAINTS_SUCCESS(200,"민원 리스트 조회에 성공하였습니다."),
    _GET_COMPLAINTS_DETAILS_SUCCESS(200,"민원 상세 조회에 성공하였습니다."),
    _GET_SCRAPS_SUCCESS(200, "스크랩 리스트 조회에 성공하였습니다."),
    _POST_SCRAPS_SUCCESS(201, "스크랩이 성공적으로 반영되었습니다."),
    _GET_DETAIL_SUCCESS(200,"민원 상세조회에 성공하였습니다."),
    _GET_CATEGORY_COMPLAINTES_SUCCESS(200, "카테고리별 민원리스트 조회에 성공하였습니다."),
    _GET_KEYWORD_COMPLAINTES_SUCCESS(200,"키워드 검색 조회에 성공하였습니다."),
    _USER_DELETED(200,"회원탈퇴에 성공하였습니다.");

    private final int code;
    private final String message;

    @Override
    public ResponseDTO getResponse() {
        return ResponseDTO.builder()
                .code(code)
                .message(message)
                .build();
    }
}
