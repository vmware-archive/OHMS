/* ********************************************************************************
 * RestTemplateTest.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.https;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;
import com.vmware.vrack.hms.rest.factory.URIBuilder;

/**
 * Test class for {@link HmsOobAgentRestTemplate}
 *
 * @author spolepalli
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/hms-spring-aggregator-test.xml" } )
public class RestTemplateTest
{

    @Before
    public void init()
    {
    }

    /**
     * This test is to ensure that Default headers are created properly
     *
     * @throws HmsException
     */
    @Test
    public void testHmsRestTemplateForHmsEntity()
        throws HmsException
    {
        HmsOobAgentRestTemplate<Object> template1 = new HmsOobAgentRestTemplate<Object>();
        HttpEntity<Object> httpEntity1 = template1.getHttpEntity();
        HttpHeaders headers1 = httpEntity1.getHeaders();
        Assert.assertNotNull( headers1 );
        Assert.assertEquals( MediaType.APPLICATION_JSON_VALUE, headers1.get( "Content-Type" ).get( 0 ) );

        HmsOobAgentRestTemplate<Object> template2 = new HmsOobAgentRestTemplate<Object>( headers1 );
        HttpEntity<Object> httpEntity2 = template2.getHttpEntity();
        HttpHeaders headers2 = httpEntity2.getHeaders();
        Assert.assertNotNull( headers2 );
        Assert.assertEquals( MediaType.APPLICATION_JSON_VALUE, headers2.get( "Content-Type" ).get( 0 ) );

        List<String> list = new ArrayList<String>();
        list.add( "Test" );
        HmsOobAgentRestTemplate<List<String>> template3 = new HmsOobAgentRestTemplate<List<String>>( list );
        HttpEntity<List<String>> httpEntity3 = template3.getHttpEntity();
        HttpHeaders headers3 = httpEntity3.getHeaders();
        Assert.assertNotNull( headers3 );
        Assert.assertEquals( MediaType.APPLICATION_JSON_VALUE, headers3.get( "Content-Type" ).get( 0 ) );
        Assert.assertEquals( "Test", httpEntity3.getBody().get( 0 ) );

        list = new ArrayList<String>();
        list.add( "Test1" );
        HmsOobAgentRestTemplate<List<String>> template4 = new HmsOobAgentRestTemplate<List<String>>( list, headers3 );
        HttpEntity<List<String>> httpEntity4 = template4.getHttpEntity();
        HttpHeaders headers4 = httpEntity4.getHeaders();
        Assert.assertNotNull( headers4 );
        Assert.assertEquals( MediaType.APPLICATION_JSON_VALUE, headers4.get( "Content-Type" ).get( 0 ) );
        Assert.assertEquals( "Test1", httpEntity4.getBody().get( 0 ) );

        list = new ArrayList<String>();
        list.add( "Test1" );
        HmsOobAgentRestTemplate<List<String>> template5 = new HmsOobAgentRestTemplate<List<String>>( list, "Test" );
        HttpEntity<List<String>> httpEntity5 = template5.getHttpEntity();
        HttpHeaders headers5 = httpEntity5.getHeaders();
        Assert.assertNotNull( headers5 );
        Assert.assertEquals( "Test", headers5.get( "Content-Type" ).get( 0 ) );
        Assert.assertEquals( "Test1", httpEntity5.getBody().get( 0 ) );
    }

    /**
     * This test is to ensure that Uri object returned is consisting of right parameters. <br/>
     * 1. url <br/>
     * 2. schema <br/>
     * 3. Port <br />
     *
     * @throws URISyntaxException
     */
    @Ignore
    @Test
    public void testIfTheUriObjectIsAsExpected()
        throws HmsException
    {
        URI uri = URIBuilder.getURI( "/path" );
        Assert.assertEquals( "/path", uri.getPath() );
        Assert.assertEquals( Constants.HTTPS, uri.getScheme() );
        Assert.assertEquals( 8450, uri.getPort() );

        uri = URIBuilder.getURI( "/path", "/query" );
        Assert.assertEquals( "/path", uri.getPath() );
        Assert.assertEquals( "/query", uri.getQuery() );
        Assert.assertEquals( Constants.HTTPS, uri.getScheme() );
        Assert.assertEquals( 8450, uri.getPort() );

        uri = URIBuilder.getURI( Constants.HTTPS, "ipaddress", 8450, "/path", "/query" );
        Assert.assertEquals( "/path", uri.getPath() );
        Assert.assertEquals( "/query", uri.getQuery() );
        Assert.assertEquals( Constants.HTTPS, uri.getScheme() );
        Assert.assertEquals( 8450, uri.getPort() );
    }
}
