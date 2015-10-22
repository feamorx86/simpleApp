package com.feamor.testing;

import com.feamor.testing.server.Config;
import com.feamor.testing.server.NettyManager;
import com.feamor.testing.server.TestConfig;
import com.feamor.testing.server.TestService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.endpoint.PollingConsumer;
import org.springframework.scheduling.TaskScheduler;

/**
 * Created by user on 04.08.2015.
 */

@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
public class Application {

    public static void main(String [] args) {

        //ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        // For the destroy method to work.
        context.registerShutdownHook();


        //TestService service = (TestService) context.getBean("test_service");
        //PollingConsumer consumer = (PollingConsumer) context.getBean("test_consumer");

        // Start tcp and flash servers
        NettyManager manager = context.getBean(NettyManager.class);
        try
        {
            manager.startServer();
        }
        catch (Exception e)
        {

        }
    }
}
