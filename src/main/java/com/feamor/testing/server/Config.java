package com.feamor.testing.server;

import com.feamor.testing.server.services.*;
import com.feamor.testing.server.utils.IdType;
import com.feamor.testing.server.utils.Ids;
import io.netty.buffer.ByteBuf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.support.Function;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.integration.endpoint.PollingConsumer;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.*;
import org.springframework.messaging.core.DestinationResolutionException;
import org.springframework.messaging.core.DestinationResolver;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by feamor on 08.10.2015.
 */
@Configuration
@ComponentScan
@EnableIntegration
@IntegrationComponentScan
public class Config {
    public static class Channels {
        public static final String NEW_MESSAGES = "ch_NewMessages";
        public static final String SEND_MESSAGES = "ch_SendMessages";
        public static final String CLIENT_MESSAGES = "ch_ClientMessages";
        public static final String GAME_MESSAGES = "ch_Game";

        public static final String ERRORS = "errorChannel";
    }

    public static class Executors {
        public static final String MESSAGES = "exe_Messages";
        public static final String UTILITY = "exe_Utility";
        public static final String GAME_LOGIC = "exe_GameLogic";
    }

    public static class EndPoints {
        public static final String SERVICES_ROUTER = "e_Roter";
        public static final String MESSAGES_SERVICE = "e_Messages";
    }

    @Bean(name = Executors.MESSAGES)
    public ThreadPoolTaskExecutor getMessagesExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10 * 10000);
        executor.setKeepAliveSeconds(2000);
        executor.setThreadGroupName(Executors.MESSAGES);
        return executor;
    }

    @Bean(name = Executors.UTILITY)
    public ThreadPoolTaskExecutor getUtilityExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10 * 10000);
        executor.setKeepAliveSeconds(2000);
        executor.setThreadGroupName(Executors.UTILITY);
        return executor;
    }

    @Bean(name = Executors.GAME_LOGIC)
    public ThreadPoolTaskExecutor getGameLogicExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10 * 10000);
        executor.setKeepAliveSeconds(2000);
        executor.setThreadGroupName(Executors.UTILITY);
        return executor;
    }

    @Bean(name = Channels.ERRORS)
    public QueueChannel getErrorsChannel() {
        QueueChannel channel = new QueueChannel(10 * 1000);
        return channel;
    }

    @Bean(name = Channels.NEW_MESSAGES)
    public QueueChannel getNewMessagesChannel() {
        QueueChannel channel = new QueueChannel(10 * 1000);
        return channel;
    }

    @Bean(name = Channels.SEND_MESSAGES)
    public QueueChannel getSendMessagesChannel() {
        QueueChannel channel = new QueueChannel(10 * 1000);
        return channel;
    }

    @Bean(name = Channels.CLIENT_MESSAGES)
    public QueueChannel getClientMessagesChannel() {
        QueueChannel channel = new QueueChannel(10 * 1000);
        return channel;
    }

    @Bean(name = Channels.GAME_MESSAGES)
    public QueueChannel getGameMessagesChannel() {
        QueueChannel channel = new QueueChannel(100 * 1000);
        return channel;
    }

    @Bean
    public NettyManager getNettyManager() {
        NettyManager manager = new NettyManager();
        return manager;
    }

    @Bean
    public UserManager getUserManager() {
        UserManager manager = new UserManager();
        return manager;
    }

    @Bean
    public PlayersDAO getPlayersDAO() {
        PlayersDAO playersDAO = new PlayersDAO();
        return playersDAO;
    }

//    @Bean
//    public Messages getMessages() {
//        Messages messages= new Messages();
//        return  messages;
//    }
}
