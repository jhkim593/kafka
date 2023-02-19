package com.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class ConsumerWakeup {

    public static final Logger logger = LoggerFactory.getLogger(ConsumerWakeup.class.getName());
    public static void main(String[] args) {

        String topicName = "simple-topic";
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.56.101:9092");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "group_01");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(props);
        kafkaConsumer.subscribe(List.of(topicName));

        Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                logger.info("main program starts to exit by calling wakeup");
                kafkaConsumer.wakeup();

                try{
                    mainThread.join();
                }catch (InterruptedException e){
                    logger.error("InterruptedException");
                }
            }

        });




        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    logger.info("record key :{} record value:{}. partition:{}  recode offset:{}", record.key(), record.value(), record.partition(),record.offset());
                }
            }
        }catch (WakeupException e){
            logger.error("wakeup exception has been called");
        }finally {
            kafkaConsumer.close();
        }
    }
}
