package com.spreadthesource.tapestry.spring.hibernate;

import java.lang.reflect.Method;

import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Invocation;
import org.apache.tapestry5.ioc.MethodAdvice;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;

public class SpringHibernateTransactionAdvisorImpl implements HibernateTransactionAdvisor
{

    private final HibernateSessionManager manager;

    /**
     * The rules for advice are the same for any method: commit on success or checked exception,
     * abort on thrown exception ... so we can use a single shared advice object.
     */
    private final MethodAdvice advice = new MethodAdvice()
    {
        public void advise(Invocation invocation)
        {
            try
            {
                // Force transaction init creation by Tapestry (this call will cause the per-thread
                // service to be instantiated)
                manager.getSession();

                invocation.proceed();
            }
            catch (RuntimeException ex)
            {
                manager.abort();

                throw ex;
            }

            // For success or checked exception, commit the transaction.

            manager.commit();
        }
    };

    public SpringHibernateTransactionAdvisorImpl(HibernateSessionManager manager)
    {
        this.manager = manager;
    }

    public void addTransactionCommitAdvice(MethodAdviceReceiver receiver)
    {
        for (Method m : receiver.getInterface().getMethods())
        {
            if (m.getAnnotation(CommitAfter.class) != null)
            {
                receiver.adviseMethod(m, advice);
            }
        }
    }

}
