package com.zksite.jeeboss.web.modules.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {


    @RequestMapping("/sayHello")
    public void sayHello(HttpServletRequest request) {
        HttpSession session = request.getSession();
        //session.setAttribute("test", "123456");
        
        System.out.println(session);
    }
}
