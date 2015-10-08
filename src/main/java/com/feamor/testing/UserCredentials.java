package com.feamor.testing;

import javax.persistence.*;

/**
 * Created by user on 09.08.2015.
 */

@Entity
@Table(name = "\"UserCredentials\"")
public class UserCredentials {
    @Id
    @SequenceGenerator(name = "UserCredentialsSequence", sequenceName = "UserCredentials", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UserCredentialsSequence")
    private int id;

    @Column(name = "type", nullable = false)
    private int type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_info")
    private UserInfo user;

    @Column(name = "field1", nullable = false, length = 100)
    private String field1;

    @Column(name = "field2", length = 100)
    private String field2;

    @Column(name = "field3", length = 100)
    private String field3;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }
}
