package com.mashibing.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyExceptionHandlerExceptionResolver extends ExceptionHandlerExceptionResolver {

    //通过构造方法的方式来添加一个参数处理器
    public MyExceptionHandlerExceptionResolver(){
        List<HandlerMethodArgumentResolver> list = new ArrayList<>();
        list.add(new ServletModelAttributeMethodProcessor(true));
        this.setCustomArgumentResolvers(list);
    }

    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {

        // 找到对应的异常处理的方法
        ServletInvocableHandlerMethod exceptionHandlerMethod = getExceptionHandlerMethod(handlerMethod, exception);
        // 判断找到的异常处理方式是否为空，如果为空，直接返回
        if (exceptionHandlerMethod == null) {
            return null;
        }

        // 在本身自带的逻辑中，参数处理器没有办法处理我们对应的参数，所以此时就要思考如何添加一个参数处理器进来
        // 我们在定义一个正常方法的时候，能否传递一个ModelAndView的对象？可以
        exceptionHandlerMethod.setDataBinderFactory(new ServletRequestDataBinderFactory(null,null));

        // 设置参数处理器，返回值处理器
        exceptionHandlerMethod.setHandlerMethodArgumentResolvers(getArgumentResolvers());
        exceptionHandlerMethod.setHandlerMethodReturnValueHandlers(getReturnValueHandlers());


        // 创建 ServletWebRequest 对象
        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        // 创建 ModelAndViewContainer 对象
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Using @ExceptionHandler " + exceptionHandlerMethod);
            }
            exceptionHandlerMethod.invokeAndHandle(webRequest,mavContainer,exception);
        }
        catch (Throwable invocationEx) {
            // Any other than the original exception (or its cause) is unintended here,
            // probably an accident (e.g. failed assertion or the like).
            // 发生异常，则直接返回
            if (invocationEx != exception && invocationEx != exception.getCause() && logger.isWarnEnabled()) {
                logger.warn("Failure in @ExceptionHandler " + exceptionHandlerMethod, invocationEx);
            }
            // Continue with default processing of the original exception...
            return null;
        }

        // 如果 mavContainer 已处理，则返回 '空的' ModelAndView 对象。
        if (mavContainer.isRequestHandled()) {
            return new ModelAndView();
        }
        // 如果 mavContainer 未处，则基于 `mavContainer` 生成 ModelAndView 对象
        else {
            ModelMap model = mavContainer.getModel();
            HttpStatus status = mavContainer.getStatus();
            // 创建 ModelAndView 对象，并设置相关属性
            ModelAndView mav = new ModelAndView(mavContainer.getViewName(), model, status);
            mav.setViewName(mavContainer.getViewName());
            if (!mavContainer.isViewReference()) {
                mav.setView((View) mavContainer.getView());
            }
            if (model instanceof RedirectAttributes) {
                Map<String, ?> flashAttributes = ((RedirectAttributes) model).getFlashAttributes();
                RequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
            }
            return mav;
        }
    }
}
