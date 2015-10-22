package com.feamor.testing.server;

import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Created by feamor on 17.10.2015.
 */

//@MessageEndpoint
public class TestService {
//    int counter = 0;
//    int summ = 0;
//
//    @ServiceActivator(inputChannel = "internal_channel", poller = @Poller(fixedDelay = "1000", taskExecutor = "ttt"), autoStartup = "true")
//    public void onMessage(Message<?> message) {
//        counter++;
//        Message<Integer> intMessage = (Message<Integer>) message;
//        summ+=intMessage.getPayload();
//        System.out.println("onMessage : counter = "+counter+", summ = "+summ);
//    }
}
