package com.feamor.testing.server;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.config.EnableIntegrationManagement;
import org.springframework.integration.config.EnablePublisher;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowDefinition;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.endpoint.PollingConsumer;
import org.springframework.integration.handler.ServiceActivatingHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.util.DynamicPeriodicTrigger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by feamor on 17.10.2015.
 */
//@Configuration
//@IntegrationComponentScan
//@EnableIntegration
//@ComponentScan
public class TestConfig {
/*
//    @Bean(name = "in_channel")
//    @BridgeTo(value = "internal_channel")
//    public DirectChannel getStartChannel(){
//        DirectChannel channel = new DirectChannel();
//        return channel;
//    }

    @Bean(name = "internal_channel")
    public QueueChannel getInternalChannel() {
        QueueChannel channel = new QueueChannel(50);
        return channel;
    }

    @MessagingGateway(name = "test_gateway",defaultReplyTimeout = "0")
    public interface TestGateway {
        @Gateway(requestChannel = "internal_channel")
        void someAction(int id);
    }

    @Bean(name = "ttt")
    public ThreadPoolTaskExecutor getMessagesExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10 * 10000);
        executor.setKeepAliveSeconds(2000);
        executor.setThreadGroupName("ttt");
        return executor;
    }

    @Bean(name = "test_service")
    public  TestService getService() {
        TestService testService = new TestService();
        return testService;
    }

//    @Bean(name = "test_handler")
//    public MessageHandler getStartHandler() {
//        MessageHandler handler;
//        handler = new ServiceActivatingHandler(getService(), "onMessage");
////        = new MessageHandler() {
////            @Override
////            public void handleMessage(Message<?> message) throws MessagingException {
////                System.out.println("Message handled : "+message);
////            }
////        };
//        return handler;
//    }

    @Bean (name = IntegrationContextUtils.TASK_SCHEDULER_BEAN_NAME)
    public TaskScheduler getMessageScheduler() {
        TaskScheduler scheduler = new ThreadPoolTaskScheduler();
        return  scheduler;
    }

    @Bean(name = "test_poller")
    public PollerMetadata getPoller() {
        PollerMetadata metadata = new PollerMetadata();
        metadata.setTaskExecutor(getMessagesExecutor());
        metadata.setReceiveTimeout(1000);
        metadata.setMaxMessagesPerPoll(5);
        metadata.setTrigger(new DynamicPeriodicTrigger(1000));
        return metadata;
    }

//    @Bean(name = "test_consumer")
//    public PollingConsumer getConsumer() {
//        PollingConsumer consumer = new PollingConsumer(getStartChannel(), getStartHandler());
//        consumer.setTaskExecutor(getMessagesExecutor());
//        consumer.setAutoStartup(true);
//        consumer.setReceiveTimeout(1000);
//        consumer.setTaskScheduler(getMessageScheduler());
//        return consumer;
//    }
*/
}
