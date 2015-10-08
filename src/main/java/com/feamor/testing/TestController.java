package com.feamor.testing;

import com.feamor.testing.dialog.DialogTester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.List;

/**
 * Created by user on 04.08.2015.
 */
@Controller
@Transactional
public class TestController {

    @Autowired
    private TestDao testDao;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        List<UserInfo> users = testDao.listUsers();
        if (users != null && users.size()>0) {
            StringBuilder builder = new StringBuilder();
            builder.append("<html><head><meta charset=\"UTF-8\"></head>" +
                    "<body><ul>");
            for(UserInfo user : users) {
                builder.append("<li>Name : "+user.getFirstName()+", second name : "+user.getSecondName()+", accounts: "+user.getCredentialses().size()+"</li>");
            }
            builder.append("</ul></body></html>");
            return builder.toString();
        } else {

            return "<body>Fail to get users or no users</body>";
        }
    }

    @RequestMapping("/")
    @ResponseBody
    public String sayTest() {
        return "<Body>This is index page<Body>";
    }

    @RequestMapping("/createData")
    @ResponseBody
    public String createTestData() {
        testDao.createUser("test", "test", "test");
        testDao.createUser("Ivanov", "Ivan", "ivan");
        return "<Body>createTestData - complete<Body>";
    }

    @Autowired
    private IntegrationConfig.UserService userService;

    @RequestMapping("/int")
    @ResponseBody
    public String integrationTest(@RequestParam(required = true, value = "id") int userId) {
        long start = Calendar.getInstance().getTimeInMillis();
        UserInfo user = userService.getUserById(userId);
        if (user == TestIntegration.nullValue)
            user = null;
        StringBuilder result = new StringBuilder();
        long finish = Calendar.getInstance().getTimeInMillis();
        long delta = finish - start;
        result.append("<body><h1>User information</h1><h2>about user #"+userId+"</h2><hr>");
        result.append("<h3>Delay: "+delta+"</h3>");
        if (user == null) {
            result.append("<h3>There is no user with such id</h3>");
        } else {
            result.append("First name : "+user.getFirstName()+"<br>");
            result.append("Second name : "+user.getSecondName()+"<br>");
            result.append("Nick : "+user.getNickName()+"<br>");
        }
        result.append("</body></html>");
        return result.toString();
    }

    @RequestMapping("/tests")
    @ResponseBody
    public String startTests() {
        StringBuilder output = new StringBuilder();
        DialogTester.tests(output);
        return "<HTML><body><h1>Tests finished</h1><h3>results:<br>"+output.toString()+"</h3></body></html>";
    }

}
