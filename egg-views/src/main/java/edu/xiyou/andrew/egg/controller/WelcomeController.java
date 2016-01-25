package edu.xiyou.andrew.egg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 欢迎页的controller
 * Created by andrew on 16-1-24.
 */
@Controller
public class WelcomeController {

    /**
     * 欢迎页
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("index.action")
    public String indexAction(HttpServletRequest httpServletRequest){
        return "index";
    }

    /**
     * 欢迎页
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("index")
    public String index(HttpServletRequest httpServletRequest){
        return "index";
    }
}
