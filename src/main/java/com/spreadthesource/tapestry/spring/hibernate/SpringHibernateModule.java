package com.spreadthesource.tapestry.spring.hibernate;

import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.hibernate.HibernateSymbols;
import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.services.AliasContribution;
import org.springframework.context.ApplicationContext;

/**
 * This module overrides defaults implements of session source and session manager. To achieve that,
 * it simply call a spring LocalSessionFactoryBean to get the SessionFactory and the spring
 * PlatformTransactionManager bean to handle transaction in your application. The objective to allow
 * you to call transactionnal methods of your Spring beans in the same transaction than the current
 * Tapestry service does.
 * 
 * @author ccordenier
 */
public class SpringHibernateModule
{

    public static void contributeApplicationDefaults(MappedConfiguration<String, String> conf)
    {
        conf.add(HibernateSymbols.DEFAULT_CONFIGURATION, "false");
    }

    /**
     * Bind tapestry-spring-tx services.
     * 
     * @param binder
     */
    public static void bind(ServiceBinder binder)
    {
        binder.bind(HibernateSessionSource.class, SpringHibernateSessionSourceImpl.class).withId(
                "springHibernateSessionSource");
        binder.bind(HibernateTransactionAdvisor.class, SpringHibernateTransactionAdvisorImpl.class)
                .withId("springTxAdvisor");
    }

    /**
     * This is the core tx management, it's a simple wrapper around Spring's
     * <code>PlatformTransactionManager</code>
     * 
     * @param source
     * @param ctx
     * @param ptManager
     * @param txBeanName
     * @return
     */
    @Scope(ScopeConstants.PERTHREAD)
    public HibernateSessionManager buildSpringHibernateSessionManager(
            @InjectService("springHibernateSessionSource") HibernateSessionSource source,
            ApplicationContext ctx, PerthreadManager ptManager,
            @Inject @Symbol(SpringHibernateConstants.TX_MANAGER_BEAN_NAME) String txBeanName)
    {
        SpringHibernateSessionManagerImpl manager = new SpringHibernateSessionManagerImpl(source,
                ctx, txBeanName);
        ptManager.addThreadCleanupListener(manager);
        return manager;
    }

    /**
     * Initialize default values for configuration properties.
     * 
     * @param configuration
     */
    public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration)
    {
        configuration.add(SpringHibernateConstants.SESSION_FACTORY_BEAN_NAME, "sessionFactory");
        configuration.add(SpringHibernateConstants.TX_MANAGER_BEAN_NAME, "transactionManager");
    }

    /**
     * Some of the core services must be overriden to allow Tapestry to lookup into your SPring
     * configuration instead of building its own Hibernate session factory.
     * 
     * @param configuration
     * @param sessionSource
     * @param manager
     * @param advisor
     */
    public static void contributeAlias(Configuration<AliasContribution> configuration,
            @InjectService("springHibernateSessionSource") HibernateSessionSource sessionSource,
            @InjectService("springHibernateSessionManager") HibernateSessionManager manager,
            @InjectService("springTxAdvisor") HibernateTransactionAdvisor advisor)
    {
        configuration.add(AliasContribution.create(HibernateSessionSource.class, sessionSource));
        configuration.add(AliasContribution.create(HibernateSessionManager.class, manager));
        configuration.add(AliasContribution.create(HibernateTransactionAdvisor.class, advisor));
    }

}
