package com.tangp.sourcingsearch;

import com.tangp.sourcingsearch.service.KafkaService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SourcingSearchApplication implements InitializingBean {

    @Autowired
    private KafkaService kafkaService;

    public static void main(String[] args) {
        SpringApplication.run(SourcingSearchApplication.class, args);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
//        kafkaService.send("topic_null", "{\"test\":123}");
    }
}
