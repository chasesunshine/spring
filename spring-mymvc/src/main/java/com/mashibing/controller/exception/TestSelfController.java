package com.mashibing.controller.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestSelfController {

    @RequestMapping("/lian")
    public ModelAndView lian(ModelAndView view){
        view.setViewName("error");
        throw new LianException();
    }
}
