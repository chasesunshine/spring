package com.mashibing.controller.async;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.WebAsyncTask;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.Callable;

@Controller
public class AsyncController {
    @ResponseBody
    @RequestMapping(value = "/webasynctask",produces = "text/plain; charset=UTF-8")
    public WebAsyncTask<String> webAsyncTask(){
        System.out.println("WebAsyncTask处理器主线程进入");
        WebAsyncTask<String> task = new WebAsyncTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(5*1000L);
                System.out.println("WebAsyncTask处理执行中。。。");
                return "久等了";
            }
        });
        System.out.println("WebAsyncTask处理器主线程退出");
        return task;
    }
}