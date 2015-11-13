package com.feamor.testing;

import com.feamor.testing.server.Config;
import com.feamor.testing.server.NettyManager;
import com.feamor.testing.server.initialization.Initializer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Map;

/**
 * Created by user on 04.08.2015.
 */

@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
public class Application {

    private static AbstractApplicationContext context;

    public static AbstractApplicationContext getContext() {
        return  context;
    }

    public static void main(String [] args) {

        //ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        context = new AnnotationConfigApplicationContext(Config.class);
        // For the destroy method to work.
        context.registerShutdownHook();


        //TestService service = (TestService) context.getBean("test_service");
        //PollingConsumer consumer = (PollingConsumer) context.getBean("test_consumer");

        // Start tcp and flash servers
        Map<String, Initializer> initializers = context.getBeansOfType(Initializer.class);
        if (initializers != null) {
            for(Initializer initializer : initializers.values()) {
                initializer.initialize();
            }
        }

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
