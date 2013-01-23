package com.britesnow.samplesocial.test;

import java.util.Map;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.britesnow.snow.testsupport.SnowTestSupport;
import com.britesnow.snow.testsupport.mock.RequestContextMock;
import com.britesnow.snow.testsupport.mock.RequestContextMockFactory.RequestMethod;

public class FacebookServiceTests  extends SnowTestSupport {
    @BeforeClass
    public static void initTestClass() throws Exception {
        SnowTestSupport.initWebApplication("src/main/webapp");
    }    
    @Test
    public void quickHSQLTest(){
        RequestContextMock rc;
        Map result;
        
        rc = requestContextFactory.createRequestContext(RequestMethod.POST, "/fb");
        webController.service(rc);
        result = rc.getResponseAsJson();
        System.out.println(result);
        Assert.assertTrue((Boolean) result.get("success"));        
    }
}
