package com.tangp.sourcingsearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class KafkaService implements ListenableFutureCallback<SendResult<String, String>> {

    //    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String payload) {
        log.info("sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload).addCallback(this);
    }

    @Override
    public void onFailure(Throwable ex) {
        log.error("send kafka error:", ex);
    }

    @Override
    public void onSuccess(SendResult<String, String> result) {
        log.info("send kafka successfully:{}", result.getRecordMetadata());
    }
}
