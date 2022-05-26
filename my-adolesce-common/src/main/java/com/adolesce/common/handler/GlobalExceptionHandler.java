package com.adolesce.common.handler;

import cn.hutool.core.util.ObjectUtil;
import com.adolesce.common.exception.BusinessException;
import com.adolesce.common.vo.ErrorResult;
import com.adolesce.common.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BusinessException.class)
    public Object errorHandler(BusinessException be, HttpServletRequest request) {
        be.printStackTrace();
        ErrorResult errorResult = be.getErrorResult();
        if(ObjectUtil.isEmpty(errorResult)){
            Object jsonMap = request.getAttribute("resultData");
            return jsonMap;
        }else{
            return Response.failure(errorResult.getErrMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    public Response handlerException(Exception ex) {
        ex.printStackTrace();
        return Response.failure(ErrorResult.error().getErrMessage());
    }
}