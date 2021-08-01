package com.nexsol.grape.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiController {

    @GetMapping("/api/version")
    @ResponseBody
    public Map apiVersion(){
        Map result = new HashMap<String, Object>();
        result.put("version","1.0.0");
        return result;
    }

}
