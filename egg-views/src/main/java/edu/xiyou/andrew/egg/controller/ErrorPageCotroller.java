package edu.xiyou.andrew.egg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by andrew on 16-1-25.
 */
@Controller
public class ErrorPageCotroller {

    @RequestMapping("/401")
    public String pageError401(){
        return "401";
    }

    @RequestMapping("/404")
    public String pageError404(){
        return "404";
    }

    @RequestMapping("500")
    public String pageError500(){
        return "500";
    }
}
