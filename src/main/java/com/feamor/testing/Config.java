package com.feamor.testing;

import com.feamor.testing.dialog.DialogManager;
import org.hibernate.SessionFactory;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by user on 08.08.2015.
 */
@Configuration
@EnableTransactionManagement
public class Config {
    @Bean(destroyMethod = "close")
    public DataSource getDataSource() {
//        ComboPooledDataSource dataSource = new ComboPooledDataSource();
//        try {
//            dataSource.setDriverClass("org.hsqldb.jdbcDriver");
//            dataSource.setJdbcUrl("jdbc:hsqldb:file:D:\\server\\hsqldb\\data\\gameserver");
//            dataSource.setUser("root");
//            dataSource.setPassword("root");
//            dataSource.setMinPoolSize(5);
//            dataSource.setMaxPoolSize(20);
//            dataSource.setMaxIdleTime(3000);
//            dataSource.setMaxStatements(50);
//            dataSource.setCheckoutTimeout(300);
//            return dataSource;
//        } catch (PropertyVetoException ex) {
//
//        }
//        return dataSource;
        PGPoolingDataSource dataSource = new PGPoolingDataSource();
        dataSource.setCharset("utf-8");
        dataSource.setMaxConnections(10);
        dataSource.setUrl("jdbc:postgresql://localhost:5432/integration");
        dataSource.setUser("test");
        dataSource.setPassword("test");
        return dataSource;

    }

    @Bean
    SessionFactory getSessionFactory() {
        Properties properties = new Properties();
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.use_sql_comments", true);
        properties.put("hibernate.connection.characterEncoding", "utf8");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
        return new LocalSessionFactoryBuilder(getDataSource())
                .scanPackages(getClass().getPackage().getName())
                .addProperties(properties)
                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .buildSessionFactory();
    }

    @Bean
    public HibernateTransactionManager transactionManager(){
        return new HibernateTransactionManager(getSessionFactory());
    }

//    @Bean
//    public HibernateTemplate hibernateTemplate() {
//        return new HibernateTemplate(getSessionFactory());
//    }

    @Bean
    public TestDao userDao() {
        return new TestDao();
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        poolTaskExecutor.setCorePoolSize(3);
        poolTaskExecutor.setMaxPoolSize(7);
        poolTaskExecutor.setQueueCapacity(20);
        return poolTaskExecutor;
    }

    @Bean
    public DialogManager dialogController() {
        DialogManager controller = new DialogManager();
        return controller;
    }

}
