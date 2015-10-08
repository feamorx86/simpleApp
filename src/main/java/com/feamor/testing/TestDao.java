package com.feamor.testing;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by user on 08.08.2015.
 */

@Transactional
public class TestDao {
    @Autowired
    private SessionFactory sessionFactory;

    public UserInfo getUserById(int id) {
        UserInfo userInfo = (UserInfo) sessionFactory.getCurrentSession()
                .createCriteria(UserInfo.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
        return  userInfo;
    }

    public List<UserInfo> listUsers() {
        List<UserInfo> users = sessionFactory.getCurrentSession()
                .createQuery("from UserInfo")
                .list();
        return users;
    }

    public UserInfo createUser(String firstName, String secondName, String nickName) {
        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName(firstName);
        userInfo.setSecondName(secondName);
        userInfo.setNickName(nickName);
        sessionFactory.getCurrentSession().save(userInfo);
        return userInfo;
    }
}
