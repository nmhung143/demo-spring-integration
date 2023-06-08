package org.spring.integration.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Author: hungnm
 * Date: ${DATE}
 */
@Configuration
@EnableIntegration
@IntegrationComponentScan
public class DemoRouter {

    @Bean
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel channel1() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel channel2() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel channel3() {
        return new DirectChannel();
    }

    @Component
    static class MessageProducer {
        @InboundChannelAdapter(channel = "inputChannel", poller = @Poller(fixedRate = "1000"))
        public Integer produce() {
            Integer[] array = {1, 2, 3};
            var number = array[new Random().nextInt(3)];
            System.out.print("Send message: ");
            System.out.println(number);
            return number;
        }
    }

    @Component
    static class Router {
        @org.springframework.integration.annotation.Router(inputChannel = "inputChannel")
        public String route(Integer number) {
            var returnChannel = "channel1";
            if (number == 2) {
                returnChannel = "channel2";
            } else if (number == 3) {
                returnChannel = "channel3";
            }
            return returnChannel;
        }
    }

    @Component
    static class MessageConsumer {
        @ServiceActivator(inputChannel = "channel1")
        public void consume1(Integer number) {
            System.out.println("Consume channel 1");
        }

        @ServiceActivator(inputChannel = "channel2")
        public void consume2(Integer number) {
            System.out.println("Consume channel 2");

        }

        @ServiceActivator(inputChannel = "channel3")
        public void consume3(Integer number) {
            System.out.println("Consume channel 3");

        }
    }


    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(DemoRouter.class);
    }
}