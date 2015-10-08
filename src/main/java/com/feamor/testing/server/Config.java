package com.feamor.testing.server;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by feamor on 08.10.2015.
 */
public class Config {
    public static class Channels {
        public static final String NEW_MESSAGES = "NewMessagesQueue";
        public static final String CLIENT_MESSAGES = "ClientMessagesQueue";

        public static final String ERRORS = "ErrorsChanne";
    }

    public static class Executors {
        public static final String MESSAGES = "MessagesExecutor";
        public static final String UTILITY = "UtilityExecutor";
    }

    public static class EndPoints {
        public static final String SERVICES_ROUTER = "ClientMessagesQueue";
    }

    @Bean
    @Qualifier(Executors.MESSAGES)
    public ThreadPoolTaskExecutor getMessagesExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10 * 10000);
        executor.setKeepAliveSeconds(2000);
        executor.setThreadGroupName(Executors.MESSAGES);
        return executor;
    }

    @Bean
    @Qualifier(Executors.UTILITY)
    public ThreadPoolTaskExecutor get√Â¯‰¯ÂÌExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10 * 10000);
        executor.setKeepAliveSeconds(2000);
        executor.setThreadGroupName(Executors.UTILITY);
        return executor;
    }




    @Bean
    @Qualifier(Channels.ERRORS)
    public QueueChannel getErrorsChannel() {
        QueueChannel channel = new QueueChannel(10 * 1000);
        return channel;
    }

    @Bean
    @Qualifier(Channels.NEW_MESSAGES)
    public QueueChannel getNewMessagesChannel() {
        QueueChannel channel = new QueueChannel(10 * 1000);
        return channel;
    }

    @Bean
    @Qualifier(Channels.CLIENT_MESSAGES)
    public QueueChannel getClientMessagesChannel() {
        QueueChannel channel = new QueueChannel(10 * 1000);
        return channel;
    }


    @Bean
    @Qualifier(EndPoints.SERVICES_ROUTER)
    public MessageChannel getServicesRouter() {
        QueueChannel channel = new QueueChannel(10 * 1000);
        return channel;
    }




}
