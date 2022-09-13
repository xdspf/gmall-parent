package com.atguigu.gmall.cart.exception;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@RestControllerAdvice  采用自动开启注解
//@ResponseBody
//@ControllerAdvice  //告诉SpringBoot 这是所有 @Controller 的统一切面
public class GlobalExceptionHander {  //全局处理异常器

    /**
     * 业务期间出现的所有异常都用 GmallException 包装
     * @param
     * @return
     */

    @ExceptionHandler(GmallException.class)
    public Result handleGmallException(GmallException exception ){

        ResultCodeEnum codeEnum = exception.getCodeEnum();
        Result<String> result = Result.build("", codeEnum);
        return result;//给前端的返回
    }

    @ExceptionHandler(NullPointerException.class)
    public String handlenullException(NullPointerException gmallException){

        return "haha";  //给前端的返回
    }


}
