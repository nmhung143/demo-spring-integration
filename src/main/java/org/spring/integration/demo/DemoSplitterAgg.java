package org.spring.integration.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Author: hungnm
 * Date: ${DATE}
 */
@Configuration
@EnableIntegration
@IntegrationComponentScan
public class DemoSplitterAgg {

    @Bean
    public MessageChannel outputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel splitChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel aggregatorChannel() {
        return new DirectChannel();
    }


    @Component
    static class MessageProducer {
        @InboundChannelAdapter(channel = "splitChannel", poller = @Poller(fixedRate = "1000"))
        public String produce() {
            return "nguyen manh hung";
        }
    }

    @Component
    static class Splitter {
        @org.springframework.integration.annotation.Splitter(inputChannel = "splitChannel", outputChannel = "aggregatorChannel")
        public List<String> split(String fullName) {
            var splitName = fullName.split(" ");
            return Arrays.asList(splitName);
        }
    }


    @Component
    static class UpperCaseService {
        @ServiceActivator(inputChannel = "outputChannel")
        public void upperCase(String name) {
            System.out.println(name);
        }
    }

    @Component
    static class Aggregator {

        @org.springframework.integration.annotation.Aggregator(inputChannel = "aggregatorChannel", outputChannel = "outputChannel")
        public String aggregate(List<String> names) {
            StringBuilder uppercaseName = new StringBuilder();
            for(var name : names) {
                uppercaseName.append(name.toUpperCase()).append(" ");
            }
            return uppercaseName.toString();
        }
    }


    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(DemoSplitterAgg.class);
    }
}