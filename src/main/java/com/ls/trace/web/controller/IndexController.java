package com.ls.trace.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 网站入口
 * @date: 2018年12月14日
 * @author: leslie.zhang
 */
@RestController
public class IndexController {

    @RequestMapping("/")
    public ModelAndView Index(){
        return new ModelAndView("/static/view/index.html");
    }
}
