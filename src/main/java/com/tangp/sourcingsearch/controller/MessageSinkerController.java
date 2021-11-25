package com.tangp.sourcingsearch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "*"
        , methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS, RequestMethod.DELETE}
        , allowedHeaders = "*")
@RequestMapping("/api/1688search")
public class MessageSinkerController {

    private static final Logger sinker = LoggerFactory.getLogger("sinker");

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @PostMapping("/sink")
    ResponseEntity<String> sinkMessageFromScrapy(@RequestBody String json) {
        sinker.info(json);
        return ResponseEntity.ok("you are all set");
    }
}
