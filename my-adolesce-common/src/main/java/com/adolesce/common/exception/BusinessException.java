package com.adolesce.common.exception;

import com.adolesce.common.vo.ErrorResult;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义业务异常类
 */
@Data
@NoArgsConstructor
public class BusinessException extends RuntimeException {
    private ErrorResult errorResult;
    public BusinessException(ErrorResult errorResult) {
        super(errorResult.getErrMessage());
        this.errorResult = errorResult;
    }
}
