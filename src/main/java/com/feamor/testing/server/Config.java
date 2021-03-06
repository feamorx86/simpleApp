package com.feamor.testing.server;

import com.feamor.testing.server.initialization.TestInitializer;
import com.feamor.testing.server.services.*;
import com.feamor.testing.server.initialization.Initializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
        public static final String SENDER = "send_Executor";
        public static final String GAME_LOGIC = "exe_GameLogic";
        public static final String GAME_LOGIC_SCHEDULER = "scheduler_Logic";
        public static final String TASKS = "task_Executor";
    }

    @Bean(name = Executors.SENDER)
    public SchedulingTaskExecutor getMessagesSender() {
        Executor executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        ConcurrentTaskExecutor taskExecutor = new ConcurrentTaskExecutor(executor);
        return taskExecutor;
    }

    @Bean(name = Executors.MESSAGES)
    public SchedulingTaskExecutor getMessagesExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10 * 10000);
        //executor.setKeepAliveSeconds(2000);
        executor.setThreadGroupName(Executors.MESSAGES);
        return executor;
    }

    @Bean(name = Executors.UTILITY)
      public SchedulingTaskExecutor getUtilityExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10 * 10000);
        //executor.setKeepAliveSeconds(2000);
        executor.setThreadGroupName(Executors.UTILITY);
        return executor;
    }

    @Bean(name = Executors.TASKS)
    public SchedulingTaskExecutor getTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10 * 10000);
//        executor.setKeepAliveSeconds(2000);
        executor.setThreadGroupName(Executors.TASKS);
        return executor;
    }

    @Bean(name = Executors.GAME_LOGIC_SCHEDULER)
    public ThreadPoolTaskScheduler getGameLogicScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(7);
        return taskScheduler;
    }

    @Bean(name = Executors.GAME_LOGIC)
    public ThreadPoolTaskExecutor getGameLogicExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(10 * 10000);
//        executor.setKeepAliveSeconds(2000);
        executor.setThreadGroupName(Executors.GAME_LOGIC);
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
        channel.setLoggingEnabled(false);
        return channel;
    }

    @Bean(name = Channels.SEND_MESSAGES)
    public QueueChannel getSendMessagesChannel() {
        QueueChannel channel = new QueueChannel(10 * 1000);
        channel.setLoggingEnabled(false);
        return channel;
    }

    @Bean(name = Channels.CLIENT_MESSAGES)
    public QueueChannel getClientMessagesChannel() {
        QueueChannel channel = new QueueChannel(10 * 1000);
        channel.setLoggingEnabled(false);
        return channel;
    }

    @Bean(name = Channels.GAME_MESSAGES)
    public QueueChannel getGameMessagesChannel() {
        QueueChannel channel = new QueueChannel(100 * 1000);
        channel.setLoggingEnabled(false);
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

    @Bean
    public  GameResolver getGameResolver() {
        GameResolver gameResolver = new GameResolver();
        return  gameResolver;
    }

    @Bean(name = "TestInitializer")
    public Initializer getTestInitializer() {
        Initializer initializer = new TestInitializer();
        return initializer;
    }
}
