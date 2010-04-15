package com.spreadthesource.tapestry.spring.hibernate;

import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.ioc.services.ThreadCleanupListener;
import org.hibernate.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Call Spring Transaction API to initialize transaction and close it when needed. This class add
 * the elements needed in the current to work with mixed spring datasource configuration.
 * 
 * @author ccordenier
 */
public class SpringHibernateSessionManagerImpl implements HibernateSessionManager,
        ThreadCleanupListener
{
    private final Session session;

    private final PlatformTransactionManager txManager;
    
    private TransactionStatus status;
    
    public SpringHibernateSessionManagerImpl(HibernateSessionSource source, ApplicationContext ctx, String txBeanName)
    {
        this.session = source.create();
        this.txManager = (PlatformTransactionManager) ctx.getBean(txBeanName);
        startNewTransaction();
    }

    private void startNewTransaction()
    {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        this.status = txManager.getTransaction(definition);
    }

    public void abort()
    {
        this.txManager.rollback(this.status);
        startNewTransaction();
    }

    public void commit()
    {
        this.txManager.commit(this.status);
        startNewTransaction();
    }

    public Session getSession()
    {
        return session;
    }

    /**
     * Rollsback the transaction at the end of the request, then closes the session. This means that
     * any uncommitted changes are lost; code should inject the HSM and invoke {@link #commit()}
     * after making any changes, if they should persist.
     */
    public void threadDidCleanup()
    {
        this.txManager.rollback(this.status);
        SessionFactoryUtils.closeSession(this.session);
    }
}