package com.spreadthesource.tapestry.spring.hibernate;

/**
 * Define here your module configuration constants.
 * 
 * @author ccordenier
 */
public abstract class SpringHibernateConstants
{

    /**
     * Bean from which the Hibernate SessionFactory will be retrieved. Default is 'sessionFactory'
     */
    public static final String SESSION_FACTORY_BEAN_NAME = "spreadthesource.session-factory-bean-name";
    
    /**
     * Specify the name of your Transaction manager using this constants. Default is 'transactionManager'
     */
    public static final String TX_MANAGER_BEAN_NAME = "spreadthesource.tx-manager-bean-name";

}
