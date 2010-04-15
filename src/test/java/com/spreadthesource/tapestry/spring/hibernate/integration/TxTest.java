package com.spreadthesource.tapestry.spring.hibernate.integration;

import org.apache.tapestry5.test.SeleniumTestCase;
import org.testng.annotations.Test;

public class TxTest extends SeleniumTestCase
{

    @Test
    public void txSuccess()
    {
        open("/index");
        waitForPageToLoad();
        click("id=test1");
        assertTrue(
                "ccordenier-tapestry-1".equals(getText("id=ccordenier-tapestry-1")),
                "Tapestry user has not been added");
        assertTrue(
                "ccordenier-spring-1".equals(getText("id=ccordenier-spring-1")),
                "Spring user has not been added");
    }

}
