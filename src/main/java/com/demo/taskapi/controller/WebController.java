package com.demo.taskapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirige a index.html.
 */
@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
}