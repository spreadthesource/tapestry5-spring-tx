package com.spreadthesource.tapestry.spring.hibernate.test.services;

import java.util.List;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;

import com.spreadthesource.tapestry.spring.hibernate.model.User;

public interface UserManager
{
    @CommitAfter
    void emulateSuccess(int i);

    @CommitAfter
    void tapestryFailure(int i);
    
    @CommitAfter
    void springFailure(int t);
    
    List<User> listUsers();
}
