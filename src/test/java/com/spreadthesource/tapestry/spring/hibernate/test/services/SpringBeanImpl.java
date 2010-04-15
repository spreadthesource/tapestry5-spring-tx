package com.spreadthesource.tapestry.spring.hibernate.test.services;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * This bean emulates access to the Database to generate a success or a failure.
 * 
 * @author ccordenier
 */
public class SpringBeanImpl implements SpringBean
{
    private JdbcTemplate template;

    public SpringBeanImpl(DataSource dataSource)
    {
        this.template = new JdbcTemplate(dataSource);
    }

    public void success(int i)
    {
        template.execute("insert into User(username) values('ccordenier-spring-" + i
                + "');");
    }

    public void failure(int i)
    {
        template.execute("insert into User(username) values(null);");
    }
}
