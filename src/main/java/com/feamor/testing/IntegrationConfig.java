package com.feamor.testing;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.core.DestinationResolutionException;
import org.springframework.messaging.core.DestinationResolver;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by user on 16.08.2015.
 */
@Configuration
public class IntegrationConfig {

    @Bean
    @Qualifier("taskExecutor")
    public ThreadPoolTaskExecutor getTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10 * 500);
        executor.setKeepAliveSeconds(200);
        executor.setThreadGroupName("task-pool");
        return executor;
    }
    public static final String INPUT_DATA_CHANNEL = "inputDataChannel";
    public static final String OUTPUT_DATA_CHANNEL = "outputDataChannel";

    @Bean
//    @Qualifier(INPUT_DATA_CHANNEL)
    public QueueChannel inputDataChannel() {
        QueueChannel channel = new QueueChannel(50);
        return channel;
    }

    @Bean
//    @Qualifier(OUTPUT_DATA_CHANNEL)
    public QueueChannel outputDataChannel() {
        QueueChannel channel = new QueueChannel(50);
        return channel;
    }

    @MessagingGateway
    public interface UserService {
        @Gateway(requestChannel = INPUT_DATA_CHANNEL, replyChannel = OUTPUT_DATA_CHANNEL)
        UserInfo getUserById(int userId);
    }

//    @Bean
//    public TestIntegration createTestIntegraion() {
//        return new TestIntegration();
//    }
}
