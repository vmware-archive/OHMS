/* ********************************************************************************
 * UpgradeRestServiceTest.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.rest.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.rest.model.OobUpgradeSpec;
import com.vmware.vrack.hms.common.rest.model.RollbackSpec;
import com.vmware.vrack.hms.common.rest.model.UpgradeStatus;

/**
 * <code>UpgradeRestServiceTest</code> is ... <br>
 *
 * @author VMware, Inc.
 */
public class UpgradeRestServiceTest
{
    /** The service. */
    private UpgradeRestService service;

    /** The message. */
    private String message = null;

    /** The upgrade spec. */
    private OobUpgradeSpec upgradeSpec;

    /** The rollback spec. */
    private RollbackSpec rollbackSpec;

    /** The Constant ROLLABACK_SCRIPT. */
    private static final String ROLLABACK_SCRIPT = "hms.rollback.script";

    /** The user home. */
    private String userHome;

    /**
     * Instantiates a new upgrade rest service test.
     */
    public UpgradeRestServiceTest()
    {
        service = new UpgradeRestService();
        upgradeSpec = new OobUpgradeSpec();
        upgradeSpec.setChecksum( "checksum" );
        upgradeSpec.setFileName( "fileName" );
        upgradeSpec.setId( "hmsToken" );
        upgradeSpec.setLocation( "location" );
        upgradeSpec.setScriptsLocation( "scriptsLocation" );
        rollbackSpec = new RollbackSpec();
        rollbackSpec.setId( "hmsToken" );
        rollbackSpec.setScriptsLocation( "scriptsLocation" );
        HmsConfigHolder.initializeHmsAppProperties();
        userHome = System.getProperty( "user.home" );
    }

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp()
        throws Exception
    {
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown()
        throws Exception
    {
    }

    /*
     * ************************************************************************** UPGRADE TESTS
     * **************************************************************************
     */
    /**
     * Test upgrade with null spec.
     */
    @Test
    public void testUpgradeWithNullSpec()
    {
        Response response = upgrade( null );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.BAD_REQUEST.getStatusCode() );
        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );
        message = "HMS upgrade request should have the mandatory parameters - "
            + "[scriptsLocation, id, location, fileName, checksum] ";
        assertEquals( message, status.getMoreInfo() );
    }

    /**
     * Test upgrade with invalid checksum.
     */
    @Test
    public void testUpgradeWithNullChecksum()
    {
        upgradeSpec.setChecksum( null );
        Response response = upgrade( upgradeSpec );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.BAD_REQUEST.getStatusCode() );
        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );
        message = "'checksum' is a mandatory parameter for HMS Upgrade.";
        assertEquals( message, status.getMoreInfo() );
    }

    /**
     * Test upgrade with blank checksum.
     */
    @Test
    public void testUpgradeWithBlankChecksum()
    {
        upgradeSpec.setChecksum( " " );
        Response response = upgrade( upgradeSpec );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.BAD_REQUEST.getStatusCode() );
        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );
        message = "'checksum' is a mandatory parameter for HMS Upgrade.";
        assertEquals( message, status.getMoreInfo() );
    }

    /**
     * Test upgrade with null file name.
     */
    @Test
    public void testUpgradeWithNullFileName()
    {
        upgradeSpec.setFileName( null );
        Response response = upgrade( upgradeSpec );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.BAD_REQUEST.getStatusCode() );
        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );
        message = "'fileName' is a mandatory parameter for HMS Upgrade.";
        assertEquals( message, status.getMoreInfo() );
    }

    /**
     * Test upgrade with null file name.
     */
    @Test
    public void testUpgradeWithBlankFileName()
    {
        upgradeSpec.setFileName( " " );
        Response response = upgrade( upgradeSpec );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.BAD_REQUEST.getStatusCode() );
        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );
        message = "'fileName' is a mandatory parameter for HMS Upgrade.";
        assertEquals( message, status.getMoreInfo() );
    }

    /**
     * Test upgrade with null file name.
     */
    @Test
    public void testUpgradeWithNullHmsToken()
    {
        upgradeSpec.setId( null );
        Response response = upgrade( upgradeSpec );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.BAD_REQUEST.getStatusCode() );
        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );
        message = "'id' is a mandatory parameter for HMS Upgrade.";
        assertEquals( message, status.getMoreInfo() );
    }

    /**
     * Test upgrade with null file name.
     */
    @Test
    public void testUpgradeWithBlankHmsToken()
    {
        upgradeSpec.setId( " " );
        Response response = upgrade( upgradeSpec );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.BAD_REQUEST.getStatusCode() );
        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );
        message = "'id' is a mandatory parameter for HMS Upgrade.";
        assertEquals( message, status.getMoreInfo() );
    }

    /**
     * Test upgrade with null file name.
     */
    @Test
    public void testUpgradeWithNullLocation()
    {
        upgradeSpec.setLocation( null );
        Response response = upgrade( upgradeSpec );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.BAD_REQUEST.getStatusCode() );
        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );
        message = "'location' is a mandatory parameter for HMS Upgrade.";
        assertEquals( message, status.getMoreInfo() );
    }

    /**
     * Test upgrade with null file name.
     */
    @Test
    public void testUpgradeWithBlankLocation()
    {
        upgradeSpec.setLocation( " " );
        Response response = upgrade( upgradeSpec );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.BAD_REQUEST.getStatusCode() );
        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );
        message = "'location' is a mandatory parameter for HMS Upgrade.";
        assertEquals( message, status.getMoreInfo() );
    }

    /**
     * Test upgrade with null file name.
     */
    @Test
    public void testUpgradeWithNullScriptsLocation()
    {
        upgradeSpec.setScriptsLocation( null );
        Response response = upgrade( upgradeSpec );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.BAD_REQUEST.getStatusCode() );
        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );
        message = "'scriptsLocation' is a mandatory parameter for HMS Upgrade.";
        assertEquals( message, status.getMoreInfo() );
    }

    /**
     * Test upgrade with null file name.
     */
    @Test
    public void testUpgradeWithBlankScriptsLocation()
    {
        upgradeSpec.setScriptsLocation( " " );
        Response response = upgrade( upgradeSpec );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.BAD_REQUEST.getStatusCode() );
        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );
        message = "'scriptsLocation' is a mandatory parameter for HMS Upgrade.";
        assertEquals( message, status.getMoreInfo() );
    }

    /**
     * Test upgrade with invalid file.
     */
    @Test
    public void testUpgradeWithInvalidFile()
    {
        Calendar cal = Calendar.getInstance();
        String timeInMillis = Long.toString( cal.getTimeInMillis() );
        upgradeSpec.setLocation( userHome );
        upgradeSpec.setFileName( timeInMillis );
        Response response = upgrade( upgradeSpec );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.FORBIDDEN.getStatusCode() );
        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );
        message = String.format( "Upgrade binary '%s' not found at '%s'.", upgradeSpec.getFileName(),
                                 upgradeSpec.getLocation() );
        assertEquals( message, status.getMoreInfo() );
    }

    /*
     * ************************************************************************** ROLLBACK TESTS
     * **************************************************************************
     */
    /**
     * Test rollback with null spec.
     */
    @Test
    public void testRollbackWithNullSpec()
    {
        HMSRestException e = rollback( null );
        message = "HMS rollback upgrade request should have the mandatory parameters - " + "[scriptsLocation, id ] ";
        assertEquals( e.getResponseErrorCode(), Status.BAD_REQUEST.getStatusCode() );
        assertEquals( message, e.getReason() );
    }

    /**
     * Test upgrade with null file name.
     */
    @Test
    public void testRollbackWithNullHmsToken()
    {
        rollbackSpec.setId( null );
        HMSRestException e = rollback( rollbackSpec );
        assertNotNull( e );
        assertEquals( e.getResponseErrorCode(), Status.BAD_REQUEST.getStatusCode() );
        message = "'id' is a mandatory parameter for HMS Upgrade rollback.";
        assertEquals( message, e.getReason() );
    }

    /**
     * Test upgrade with null file name.
     */
    @Test
    public void testRollbackWithBlankHmsToken()
    {
        rollbackSpec.setId( " " );
        HMSRestException e = rollback( rollbackSpec );
        assertNotNull( e );
        assertEquals( e.getResponseErrorCode(), Status.BAD_REQUEST.getStatusCode() );
        message = "'id' is a mandatory parameter for HMS Upgrade rollback.";
        assertEquals( message, e.getReason() );
    }

    /**
     * Test upgrade with null file name.
     */
    @Test
    public void testRollbackWithNullScriptsLocation()
    {
        rollbackSpec.setScriptsLocation( null );
        HMSRestException e = rollback( rollbackSpec );
        assertNotNull( e );
        assertEquals( e.getResponseErrorCode(), Status.BAD_REQUEST.getStatusCode() );
        message = "'scriptsLocation' is a mandatory parameter for HMS Upgrade rollback.";
        assertEquals( message, e.getReason() );
    }

    /**
     * Test upgrade with null file name.
     */
    @Test
    public void testRollbackWithBlankScriptsLocation()
    {
        rollbackSpec.setScriptsLocation( " " );
        HMSRestException e = rollback( rollbackSpec );
        assertNotNull( e );
        assertEquals( e.getResponseErrorCode(), Status.BAD_REQUEST.getStatusCode() );
        message = "'scriptsLocation' is a mandatory parameter for HMS Upgrade rollback.";
        assertEquals( message, e.getReason() );
    }

    /**
     * Test rollback with invalid scripts location.
     */
    @Test
    public void testRollbackWithInvalidScriptsLocation()
    {
        Calendar cal = Calendar.getInstance();
        String timeInMillis = Long.toString( cal.getTimeInMillis() );
        rollbackSpec.setScriptsLocation( userHome + timeInMillis );
        HMSRestException e = rollback( rollbackSpec );
        assertNotNull( e );
        assertEquals( e.getResponseErrorCode(), Status.FORBIDDEN.getStatusCode() );
        String rollbackScript = FilenameUtils.concat( rollbackSpec.getScriptsLocation(),
                                                      HmsConfigHolder.getHMSConfigProperty( ROLLABACK_SCRIPT ) );
        message = String.format( "Upgrade rollback Script %s not found.", rollbackScript );
        assertEquals( message, e.getReason() );
    }

    /**
     * Upgrade.
     *
     * @param spec the spec
     * @return the HMS rest exception
     */
    private Response upgrade( final OobUpgradeSpec spec )
    {
        Response response = null;
        try
        {
            response = service.upgrade( spec );
        }
        catch ( HMSRestException e )
        {
            return response;
        }
        return response;
    }

    /**
     * Rollback.
     *
     * @param spec the spec
     * @return the HMS rest exception
     */
    private HMSRestException rollback( final RollbackSpec spec )
    {
        try
        {
            service.rollback( spec );
        }
        catch ( HMSRestException e )
        {
            return e;
        }
        return null;
    }
}
