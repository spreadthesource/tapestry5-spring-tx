package com.spreadthesource.tapestry.spring.hibernate;

import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.ioc.services.ThreadCleanupListener;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateAccessor;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Call Spring Transaction API to initialize transaction and close it when needed. This class add
 * the elements needed in the current to work with mixed spring datasource configuration.
 * 
 * @author ccordenier
 */
public class SpringHibernateSessionManagerImpl extends HibernateAccessor implements HibernateSessionManager,
        ThreadCleanupListener
{
    private final Session session;

    private final PlatformTransactionManager txManager;
    
    private final HibernateSessionSource source;
    
    private TransactionStatus status;
    
    private FlushMode previousFlushMode;

    private boolean existingTransaction;

    private SessionHolder sessionHolder;
    
    public SpringHibernateSessionManagerImpl(HibernateSessionSource source, ApplicationContext ctx, String txBeanName)
    {
        this.session = source.create();
        this.source = source;
        this.session.setFlushMode(FlushMode.COMMIT);
        this.txManager = (PlatformTransactionManager) ctx.getBean(txBeanName);
        startNewTransaction();
    }

    private void startNewTransaction()
    {
        sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(getSessionFactory());

        existingTransaction = (sessionHolder != null && sessionHolder.containsSession(session));
        if (existingTransaction) {
            logger.debug("Found thread-bound Session for HibernateInterceptor");
        }
        else {
            if (sessionHolder != null) {
                sessionHolder.addSession(session);
            }
            else {
                TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));
            }
        }
        
        this.previousFlushMode = applyFlushMode(session, existingTransaction);
        enableFilters(session);
        
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
        this.flushIfNecessary(session, existingTransaction);
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

        if (existingTransaction) {
            logger.debug("Not closing pre-bound Hibernate Session after HibernateInterceptor");
            disableFilters(session);
            if (previousFlushMode != null) {
                session.setFlushMode(previousFlushMode);
            }
        }
        else {
            SessionFactoryUtils.closeSession(session);
            if (sessionHolder == null || sessionHolder.doesNotHoldNonDefaultSession()) {
                TransactionSynchronizationManager.unbindResource(getSessionFactory());
            }
        }

    }

    @Override
    public SessionFactory getSessionFactory()
    {
        return this.source.getSessionFactory();
    }
}