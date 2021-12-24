package com.tangp.sourcingsearch.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tangp.sourcingsearch.service.KafkaService;
import com.tangp.sourcingsearch.service.LogSinkService;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.tangp.sourcingsearch.controller.MessageSinkerController.ResultVO.OK;

@RestController
@CrossOrigin(origins = "*"
        , methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS, RequestMethod.DELETE}
        , allowedHeaders = "*")
@RequestMapping("/api/1688search")
public class MessageSinkerController {

    @Autowired
    private LogSinkService logSinkService;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss Z");

    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @PostMapping("/sink")
    ResponseEntity<? extends Object> sinkMessageFromScrapy(@RequestBody String json
            , @RequestHeader(name = "Referer", required = false, defaultValue = "null") String refer
            , @RequestHeader(name = "X-Source", required = false, defaultValue = "1688") String source) {
        JSONObject jsonObject = JSON.parseObject(json);
        jsonObject.put("source", source);
        jsonObject.put("refer", refer);
        jsonObject.put("sink_time", SIMPLE_DATE_FORMAT.format(new Date()));
        jsonObject.put("day", DAY_FORMAT.format(new Date()));

        logSinkService.flushReceivedJson(jsonObject);
        return ResponseEntity.ok(OK);
    }

    @Data
    @Accessors(chain = true)
    public static class ResultVO implements Serializable {
        private String status;

        public static final ResultVO OK = new ResultVO().setStatus("ok");
    }
}
