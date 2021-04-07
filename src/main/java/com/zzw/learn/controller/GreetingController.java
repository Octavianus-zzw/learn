package com.zzw.learn.controller;

import com.zzw.learn.model.Greeting;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/greet")
public class GreetingController {

    @RequestMapping(value = "/item")
    public Greeting item(@RequestParam(value = "id") Long id, @RequestParam(value = "content") String content) {
        Greeting greeting = new Greeting(id, content);
        return greeting;
    }

}
