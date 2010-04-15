package com.spreadthesource.tapestry.spring.hibernate.test.services;

import java.util.List;

import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.ApplicationContext;

import com.spreadthesource.tapestry.spring.hibernate.model.User;

public class UserManagerImpl implements UserManager
{

    private final HibernateSessionManager source;

    private final SpringBean springBean;

    public UserManagerImpl(HibernateSessionManager source, ApplicationContext context)
    {
        super();
        this.source = source;
        this.springBean = (SpringBean) context.getBean("springBean");
    }

    public void springFailure(int i)
    {
        this.source.getSession();
        User user = new User();
        user.setUsername("ccordenier-tapestry-" + i);
        source.getSession().persist(user);
        // This line will fail and the transaction must be rolled back.
        this.springBean.failure(i);
    }

    public void tapestryFailure(int i)
    {
        this.springBean.success(i);
        User user = new User();
        // This line will fail and the transaction must be rolled back.
        source.getSession().persist(user);
    }

    public void emulateSuccess(int i)
    {
        this.springBean.success(i);
        User user = new User();
        user.setUsername("ccordenier-tapestry-" + i);
        source.getSession().persist(user);
    }

    @Override
    public List<User> listUsers()
    {
        Criteria crit = this.source.getSession().createCriteria(User.class);
        crit.add(Restrictions.like("username", "%"));
        return crit.list();
    }

}
