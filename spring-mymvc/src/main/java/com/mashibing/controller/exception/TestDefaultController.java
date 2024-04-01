package com.mashibing.controller.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TestDefaultController {

    @RequestMapping("/default")
    public ModelAndView noHandlerMethod(ModelAndView view, HttpServletRequest request) throws NoHandlerFoundException {
        view.setViewName("error");
        throw new NoHandlerFoundException(null,null,null);
    }
}
