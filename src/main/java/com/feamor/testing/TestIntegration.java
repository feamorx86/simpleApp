package com.feamor.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by user on 16.08.2015.
 */
@MessageEndpoint
public class TestIntegration {
    @Autowired
    private TestDao testDao;
    public static UserInfo nullValue = new UserInfo();
    @ServiceActivator(poller = @Poller(taskExecutor = "taskExecutor", fixedRate = "200", maxMessagesPerPoll = "10"),
            inputChannel = IntegrationConfig.INPUT_DATA_CHANNEL, outputChannel = IntegrationConfig.OUTPUT_DATA_CHANNEL)
    public UserInfo userById(int userId) {
        UserInfo user = testDao.getUserById(userId);
        if (user == null) {
          user = nullValue;
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return user;
    }
}
