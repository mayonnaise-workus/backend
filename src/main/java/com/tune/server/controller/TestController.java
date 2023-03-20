package com.tune.server.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(tags = {"Health API"})
public class TestController {
    @GetMapping("/")
    public String index() {
        return "Health";
    }

}
