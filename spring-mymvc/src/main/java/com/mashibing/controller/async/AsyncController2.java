package com.mashibing.controller.async;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;
@Controller
public class AsyncController2 {
    @ResponseBody
    @RequestMapping(value = "/callable",produces = "text/plain; charset=UTF-8")
    public Callable<String> callable(){
        System.out.println("Callable处理器主线程进入");
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(5 * 1000L);
                System.out.println("Callable处理执行中。。。");
                return "久等了";
            }
        };
        System.out.println("Callable处理器主线程退出");
        return callable;
    }
}