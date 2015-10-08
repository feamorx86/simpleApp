package com.feamor.testing;

import org.hibernate.dialect.HSQLDialect;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 08.08.2015.
 */

@Entity
@Table(name = "\"UserInfo\"")
public class UserInfo {
    @Id
    @SequenceGenerator(name="UserInfoSequence",sequenceName="UserInfoSequence", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.AUTO, generator="UserInfoSequence")
    @Column(name="id", unique=true, nullable=false)
    private int id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "second_name", nullable = false, length = 100)
    private String secondName;

    @Column(name = "nick_name", length = 100)
    private String nickName;

    @OneToMany(mappedBy = "user")
    private Set<UserCredentials> credentialses;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Set<UserCredentials> getCredentialses() {
        return credentialses;
    }

    public void setCredentialses(Set<UserCredentials> credentialses) {
        this.credentialses = credentialses;
    }
}
