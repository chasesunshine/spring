package com.mashibing.controller;

import com.mashibing.bean.User;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class HelloController2 extends AbstractController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("hahha");
        List<com.mashibing.bean.User> userList = new ArrayList<>();
        com.mashibing.bean.User user1 = new com.mashibing.bean.User("张三", 12);
        com.mashibing.bean.User user2 = new User("李四", 21);
        userList.add(user1);
        userList.add(user2);
        return new ModelAndView("userlist", "users", userList);
    }
}
