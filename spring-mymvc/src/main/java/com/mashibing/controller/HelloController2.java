package src.main.java.com.mashibing.controller;

import src.main.java.com.mashibing.bean.User;
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
        List<src.main.java.com.mashibing.bean.User> userList = new ArrayList<>();
        src.main.java.com.mashibing.bean.User user1 = new src.main.java.com.mashibing.bean.User("张三", 12);
        src.main.java.com.mashibing.bean.User user2 = new User("李四", 21);
        userList.add(user1);
        userList.add(user2);
        return new ModelAndView("userlist", "users", userList);
    }
}
