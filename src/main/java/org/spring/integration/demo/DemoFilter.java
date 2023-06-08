package org.spring.integration.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Author: hungnm
 * Date: ${DATE}
 */
@Configuration
@EnableIntegration
@IntegrationComponentScan
public class DemoFilter {

    @Bean
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel outputChannel() {
        return new DirectChannel();
    }


    @Component
    static class Filter {
        @org.springframework.integration.annotation.Filter(inputChannel = "inputChannel", outputChannel = "outputChannel")
        public boolean accept(Message<?> message) {
            return message.getPayload().equals("first line!");
        }
    }

    @Component
    static class MessageProducer {
        @InboundChannelAdapter(channel = "inputChannel", poller = @Poller(fixedRate = "1000"))
        public String produce() {
            String[] array = {"first line!", "second line!", "third line!"};
            return array[new Random().nextInt(3)];
        }
    }

    @Component
    static class MessageConsumer {
        @ServiceActivator(inputChannel = "outputChannel")
        public void consume(String message) {
            System.out.println(message);
        }
    }

//    @Component
//    static class Transformer {
//        @org.springframework.integration.annotation.Transformer(inputChannel = "inputChannel", outputChannel = "outputChannel")
//        public String transform(String message) {
//            return "hello " +  message;
//        }
//    }

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("classpath:ioadapter.xml");
//        ApplicationContext context = new AnnotationConfigApplicationContext(DemoFilter.class);
    }
}