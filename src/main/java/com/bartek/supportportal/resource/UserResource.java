package com.bartek.supportportal.resource;

import com.bartek.supportportal.exception.ExceptionHandling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource extends ExceptionHandling {

    @GetMapping("/test")
    public String test() {
        return "app works";
    }
}
