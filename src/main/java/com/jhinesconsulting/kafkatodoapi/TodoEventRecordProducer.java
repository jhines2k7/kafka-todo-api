package com.jhinesconsulting.kafkatodoapi;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class TodoEventRecordProducer {
    private Producer kafkaProducer;

    public TodoEventRecordProducer(Producer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void send(ProducerRecord producerRecord) {
        kafkaProducer.send(producerRecord, (RecordMetadata metadata, Exception exception) -> {
            if (exception == null) {
                System.out.println(metadata);
            } else {
                exception.printStackTrace();
            }
        });

//        kafkaProducer.flush();
//        kafkaProducer.close();
    }
}
