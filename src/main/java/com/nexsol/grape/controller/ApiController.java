package com.nexsol.grape.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ApiController {

    @GetMapping("/api/version")
    @ResponseBody
    public Map apiVersion(){
        Map result = new HashMap<String, Object>();
        result.put("version","1.0.0");
        return result;
    }

}
