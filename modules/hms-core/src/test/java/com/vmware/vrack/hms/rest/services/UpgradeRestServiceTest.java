/* ********************************************************************************
 * UpgradeRestServiceTest.java
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

package com.vmware.vrack.hms.rest.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Calendar;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.rest.model.OobUpgradeSpec;
import com.vmware.vrack.hms.common.rest.model.RollbackSpec;
import com.vmware.vrack.hms.common.rest.model.UpgradeStatus;
import com.vmware.vrack.hms.common.util.ProcessUtil;
import com.vmware.vrack.hms.utils.UpgradeUtil;

/**
 * <code>UpgradeRestServiceTest</code> is ... <br>
 *
 * @author VMware, Inc.
 */
@RunWith( PowerMockRunner.class )
@FixMethodOrder( MethodSorters.NAME_ASCENDING )
@PrepareForTest( { ProcessUtil.class } )
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

        rollbackSpec = new RollbackSpec();
        rollbackSpec.setId( "hmsToken" );

        HmsConfigHolder.initializeHmsAppProperties();
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

        message = "HMS upgrade request should have the mandatory parameters - [ id, fileName, checksum ].";
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
     * Test upgrade with invalid file.
     */
    @Test
    public void testUpgradeWithInvalidFile()
    {

        Calendar cal = Calendar.getInstance();
        String timeInMillis = Long.toString( cal.getTimeInMillis() );
        upgradeSpec.setId( "hmsToken" );
        upgradeSpec.setFileName( timeInMillis );

        Response response = upgrade( upgradeSpec );
        assertNotNull( response );

        assertTrue( response.getStatus() == Status.FORBIDDEN.getStatusCode() );

        UpgradeStatus status = (UpgradeStatus) response.getEntity();
        assertNotNull( status );

        message = String.format( "Upgrade binary '%s' not found at '%s'.", upgradeSpec.getFileName(),
                                 UpgradeUtil.getUpgradeDir( "hmsToken" ) );

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
        message = "HMS rollback upgrade request should have the mandatory parameters - [ id ].";
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
     * Test Restart proxy
     *
     * @throws HMSRestException
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testRestartProxyOnSuccessfulExecOfScript()
        throws HMSRestException
    {

        PowerMockito.mockStatic( ProcessUtil.class );
        when( ProcessUtil.executeCommand( ( any( List.class ) ) ) ).thenReturn( 0 );

        Response response = service.restartProxy( "100" );
        assertNotNull( response );
        assertTrue( response.getStatus() == Status.ACCEPTED.getStatusCode() );

    }

    /**
     * Test Restart proxy
     *
     * @throws HMSRestException
     */
    @SuppressWarnings( "unchecked" )
    @Test( expected = HMSRestException.class )
    public void testRestartProxyOnUnsuccessfulExecOfScript()
        throws HMSRestException
    {

        PowerMockito.mockStatic( ProcessUtil.class );
        when( ProcessUtil.executeCommand( ( any( List.class ) ) ) ).thenReturn( 1 );
        service.restartProxy( "100" );

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
