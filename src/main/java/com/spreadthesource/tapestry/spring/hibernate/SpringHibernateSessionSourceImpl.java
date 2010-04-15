package com.spreadthesource.tapestry.spring.hibernate;

import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/**
 * This class allows spring to initialize the transaction synchronization. Simple wrapper around
 * existing Tapestry default HibernateSessionSource and Hibernate/Spring utility classes.
 * 
 * @author ccordenier
 */
public class SpringHibernateSessionSourceImpl implements HibernateSessionSource,
        RegistryShutdownListener
{
    private final SessionFactory sessionFactory;

    private final LocalSessionFactoryBean sessionFactoryBean;
    
    public SpringHibernateSessionSourceImpl(ApplicationContext context,
            @Inject @Symbol(SpringHibernateConstants.SESSION_FACTORY_BEAN_NAME) String beanName)
    {
        this.sessionFactory = (SessionFactory) context.getBean(beanName);
        this.sessionFactoryBean = (LocalSessionFactoryBean) context.getBean(TapestryLocalSessionFactoryBean.class);
    }

    public Session create()
    {
        return sessionFactory.openSession();
    }

    public SessionFactory getSessionFactory()
    {
        return this.sessionFactory;
    }

    public Configuration getConfiguration()
    {
        return this.sessionFactoryBean.getConfiguration();
    }

    public void registryDidShutdown()
    {
        this.sessionFactory.close();
    }
}
