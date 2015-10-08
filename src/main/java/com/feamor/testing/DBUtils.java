package com.feamor.testing;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by user on 09.08.2015.
 */

public class DBUtils {

    @Autowired
    private SessionFactory sessionFactory;

    private String path;

    public void storeDbToFile() {
        String saveCommand = "Script '"+path+"'";

        sessionFactory.getCurrentSession()
                .createSQLQuery(saveCommand)
                .executeUpdate();
    }
}
