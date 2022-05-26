package com.adolesce.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResult {
    private String errCode;
    private String errMessage;

    public static ErrorResult error() {
        return ErrorResult.builder().errCode("999999").errMessage("系统异常稍后再试").build();
    }

    public static ErrorResult sendCodeFail() {
        return ErrorResult.builder().errCode("000001").errMessage("发送验证码失败").build();
    }

    public static ErrorResult checkCodeError() {
        return ErrorResult.builder().errCode("000002").errMessage("验证码错误或者失效").build();
    }

    public static ErrorResult codeNoFailure() {
        return ErrorResult.builder().errCode("000003").errMessage("验证码还未失效").build();
    }
}