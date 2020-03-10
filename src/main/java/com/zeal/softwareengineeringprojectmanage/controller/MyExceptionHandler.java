package com.zeal.softwareengineeringprojectmanage.controller;

;
import com.zeal.softwareengineeringprojectmanage.exception.UserNotFound;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(UserNotFound.class)
    public String handlerException(Exception e ,HttpServletRequest request){
        Map<String,Object> map=new HashMap<>();
        request.setAttribute("javax.servlet.error.status_code",500);
        map.put("code","user.notexist");
        map.put("message","用户出错");

        request.setAttribute("ext",map);
        //转发到/error
        return "forward:/error";
    }

}
