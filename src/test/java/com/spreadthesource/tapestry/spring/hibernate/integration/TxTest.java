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
        waitForPageToLoad();
        
        assertTrue(
                "ccordenier-tapestry-1".equals(getText("id=ccordenier-tapestry-1")),
                "Tapestry user has not been added");
        assertTrue(
                "ccordenier-spring-1".equals(getText("id=ccordenier-spring-1")),
                "Spring user has not been added");
    }
    
    @Test
    public void tapestryTxFailure() {

        open("/index");
        waitForPageToLoad();
        click("id=test2");
        waitForPageToLoad();
        
        open("/index");
        waitForPageToLoad();
        assertFalse(this.isElementPresent("id=ccordenier-tapestry-2"));
        assertFalse(this.isElementPresent("id=ccordenier-spring-2"));
    }

    
    @Test
    public void springTxFailure() {
        
        open("/index");
        waitForPageToLoad();
        click("id=test3");
        waitForPageToLoad();
        
        open("/index");
        waitForPageToLoad();
        assertFalse(this.isElementPresent("id=ccordenier-tapestry-3"));
        assertFalse(this.isElementPresent("id=ccordenier-spring-3"));
    }
}
