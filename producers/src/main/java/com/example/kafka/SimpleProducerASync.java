package com.example.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SimpleProducerASync {
    public static final Logger logger = LoggerFactory.getLogger(SimpleProducerASync.class.getName());

    public static void main(String[] args) {

        String topicName = "simple-topic";

        Properties props = new Properties();

        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.56.101:9092");
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, "helloWorld");

        kafkaProducer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception exception) {

                if(exception == null){
                    logger.info("record metadata received\n"
                            +"partition"+recordMetadata.partition()+"\n"
                            +"offset"+recordMetadata.offset());
                }
                else {
                    logger.error("error", exception.getMessage());
                }
            }
        });

        kafkaProducer.flush();


    }
}
