package com.spreadthesource.tapestry.spring.hibernate.test.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.SubModule;

import com.spreadthesource.tapestry.spring.hibernate.SpringHibernateModule;

@SubModule(SpringHibernateModule.class)
public class AppModule
{

    public static void bind(ServiceBinder binder)
    {
        binder.bind(UserManager.class, UserManagerImpl.class);
    }

    public void contributeApplicationDefaults(MappedConfiguration<String, String> configuration)
    {
        configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
    }
    
    @Match("*Manager")
    public static void adviseTransactions(HibernateTransactionAdvisor advisor,
            MethodAdviceReceiver receiver)
    {
        advisor.addTransactionCommitAdvice(receiver);
    }

}
