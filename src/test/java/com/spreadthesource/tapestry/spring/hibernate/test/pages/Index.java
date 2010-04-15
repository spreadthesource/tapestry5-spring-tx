package com.spreadthesource.tapestry.spring.hibernate.test.pages;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.spreadthesource.tapestry.spring.hibernate.model.User;
import com.spreadthesource.tapestry.spring.hibernate.test.services.UserManager;

public class Index
{

    @Inject
    private UserManager userManager;

    @Property
    private List<User> users;

    @Property
    private User currentUser;

    @SetupRender
    void setupUsers()
    {
        this.users = userManager.listUsers();
    }

    void onTapestryTxFailure(int index)
    {
        this.userManager.tapestryFailure(index);
    }

    void onSpringTxFailure(int index)
    {
        this.userManager.springFailure(index);
    }

    void onTxSuccess(int index)
    {
        this.userManager.emulateSuccess(index);
    }

}
