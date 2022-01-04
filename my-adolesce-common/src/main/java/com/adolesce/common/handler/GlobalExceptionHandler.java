package com.adolesce.common.handler;

import com.adolesce.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 */
@Slf4j
//@RestControllerAdvice
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BusinessException.class)
    public Object errorHandler(BusinessException ex, HttpServletRequest request) {
        Object jsonMap = request.getAttribute("resultData");
        return jsonMap;
    }
}