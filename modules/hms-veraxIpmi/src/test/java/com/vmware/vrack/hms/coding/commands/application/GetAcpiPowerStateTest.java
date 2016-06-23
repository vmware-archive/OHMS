package com.vmware.vrack.hms.coding.commands.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.veraxsystems.vximpi.test.CoderTest;
import com.veraxsystems.vxipmi.api.async.ConnectionHandle;
import com.veraxsystems.vxipmi.api.sync.IpmiConnector;
import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.PrivilegeLevel;
import com.veraxsystems.vxipmi.coding.payload.CompletionCode;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.payload.lan.IpmiLanResponse;
import com.veraxsystems.vxipmi.coding.payload.lan.NetworkFunction;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.veraxsystems.vxipmi.coding.protocol.IpmiMessage;
import com.veraxsystems.vxipmi.coding.protocol.Ipmiv20Message;
import com.veraxsystems.vxipmi.coding.security.CipherSuite;
import com.veraxsystems.vxipmi.common.PropertiesManager;
import com.veraxsystems.vxipmi.common.TypeConverter;
import com.vmware.vrack.coding.commands.IpmiCommandCodes;
import com.vmware.vrack.coding.commands.IpmiCommandParameters;
import com.vmware.vrack.coding.commands.application.DevicePowerState;
import com.vmware.vrack.coding.commands.application.GetAcpiPowerState;
import com.vmware.vrack.coding.commands.application.GetAcpiPowerStateResponseData;
import com.vmware.vrack.coding.commands.application.SystemPowerState;

/**
 * Test Class for GetAcpiPowerState
 * 
 * @author Yagnesh Chawda
 */
public class GetAcpiPowerStateTest
{
    private CipherSuite cs;

    private static IpmiConnector connector;

    private static ConnectionHandle handle;

    private static Logger logger = Logger.getLogger( CoderTest.class );

    private static final int PORT = 0;

    @Before
    public void setUp()
        throws Exception
    {
        // super.setUp();
        PropertiesManager.getInstance().setProperty( "timeout", "30000" );
        connector = new IpmiConnector( PORT );
        handle = connector.createConnection( InetAddress.getByName( "10.28.197.204" ) );
        cs = connector.getAvailableCipherSuites( handle ).get( 3 );
        connector.getChannelAuthenticationCapabilities( handle, cs, PrivilegeLevel.Administrator );
        // connector.openSession(handle, "vmware", "ca$hc0w", null);
        System.out.println( "Channel authentication capabilities received" );
        connector.openSession( handle, "ADMIN", "ADMIN", null );
        System.out.println( "Session open" );
    }

    @After
    public void tearDown()
        throws Exception
    {
        // super.tearDown();
        if ( handle != null )
        {
            connector.closeSession( handle );
            connector.closeConnection( handle );
            connector.tearDown();
        }
    }

    /**
     * Test for GetCommandCode,returns Command Code for Get ACPI Power State
     */
    @Test
    public void testGetCommandCode()
    {
        GetAcpiPowerState getAcpiPowerState = new GetAcpiPowerState( IpmiVersion.V20, cs, AuthenticationType.RMCPPlus,
                                                                     IpmiCommandParameters.GET_ACPI_POWER_STATE_PARAM );
        assertEquals( IpmiCommandCodes.GET_ACPI_POWER_STATE, getAcpiPowerState.getCommandCode() );
    }

    /**
     * Test for GetNetworkFunction, returns Network Function for Get ACPI Power State
     */
    @Test
    public void testGetNetworkFunction()
    {
        GetAcpiPowerState getAcpiPowerState = new GetAcpiPowerState( IpmiVersion.V20, cs, AuthenticationType.RMCPPlus,
                                                                     IpmiCommandParameters.GET_ACPI_POWER_STATE_PARAM );
        assertEquals( NetworkFunction.ApplicationRequest, getAcpiPowerState.getNetworkFunction() );
    }

    /**
     * Test GetResponseData for Nodes which are Powered Up, They Should return System Acpi Power State as S0_G0
     */
    @Test
    public void testGetResponseData_NodeOn()
        throws InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, IPMIException
    {
        GetAcpiPowerState getAcpiPowerState = new GetAcpiPowerState( IpmiVersion.V20, cs, AuthenticationType.RMCPPlus,
                                                                     IpmiCommandParameters.GET_ACPI_POWER_STATE_PARAM );
        IpmiMessage message = new Ipmiv20Message( null );
        // byte[0] is requester Address.
        // byte[1] contains both NetWork Function & requester Logical Unit Number
        // byte[2] is checkSum Data
        // byte[3] is Responder Address
        // byte[4] contains Sequence Number and responder Logical Unit NUmber
        // byte[5] is command
        // byte[6] is completion code
        // from byte[7] onwards it is the Payload Data
        int[] rawArray = { -127, 28, 99, 32, 8, 7, 0, 0, 0, -47 };
        byte[] respDataArr = new byte[10];
        for ( int i = 0; i < rawArray.length; i++ )
        {
            respDataArr[i] = (byte) rawArray[i];
            System.out.println( respDataArr[i] );
        }
        IpmiLanResponse payload = new IpmiLanResponse( respDataArr );
        payload.setCommand( getAcpiPowerState.getCommandCode() );
        payload.setCompletionCode( TypeConverter.intToByte( CompletionCode.Ok.getCode() ) );
        message.setPayload( payload );
        GetAcpiPowerStateResponseData responseData =
            (GetAcpiPowerStateResponseData) getAcpiPowerState.getResponseData( message );
        assertNotNull( responseData );
        assertEquals( DevicePowerState.D0, responseData.getDeviceAcpiPowerState() );
        assertEquals( SystemPowerState.S0_G0, responseData.getSystemAcpiPowerState() );
    }

    /**
     * Test GetResponseData for Nodes which are Powered Down, They Should return System Acpi Power State as Legacy_Off
     */
    @Test
    public void testGetResponseData_NodeOff()
        throws Exception
    {
        handle = connector.createConnection( InetAddress.getByName( "10.28.197.202" ) );
        cs = connector.getAvailableCipherSuites( handle ).get( 3 );
        connector.getChannelAuthenticationCapabilities( handle, cs, PrivilegeLevel.Administrator );
        connector.openSession( handle, "vmware", "ca$hc0w", null );
        GetAcpiPowerState getAcpiPowerState = new GetAcpiPowerState( IpmiVersion.V20, cs, AuthenticationType.RMCPPlus,
                                                                     IpmiCommandParameters.GET_ACPI_POWER_STATE_PARAM );
        IpmiMessage message = new Ipmiv20Message( null );
        // byte[0] is requester Address.
        // byte[1] contains both NetWork Function & requester Logical Unit Number
        // byte[2] is checkSum Data
        // byte[3] is Responder Address
        // byte[4] contains Sequence Number and responder Logical Unit NUmber
        // byte[5] is command
        // byte[6] is completion code
        // from byte[7] onwards it is the Payload Data
        // byte[last] is Checksum2 for payload data
        int[] rawArray = { -127, 28, 99, 32, 8, 7, 0, 33, 0, -80 };
        byte[] respDataArr = new byte[10];
        for ( int i = 0; i < rawArray.length; i++ )
        {
            respDataArr[i] = (byte) rawArray[i];
            // System.out.println(respDataArr[i]);
        }
        IpmiLanResponse payload = new IpmiLanResponse( respDataArr );
        payload.setCommand( getAcpiPowerState.getCommandCode() );
        payload.setCompletionCode( TypeConverter.intToByte( CompletionCode.Ok.getCode() ) );
        message.setPayload( payload );
        GetAcpiPowerStateResponseData responseData =
            (GetAcpiPowerStateResponseData) getAcpiPowerState.getResponseData( message );
        assertNotNull( responseData );
        assertEquals( DevicePowerState.D0, responseData.getDeviceAcpiPowerState() );
        assertEquals( SystemPowerState.Legacy_Off, responseData.getSystemAcpiPowerState() );
    }

    /**
     * Test GetResponseData for Nodes which are Powered Up,
     */
    @Test( expected = IllegalArgumentException.class )
    public void testGetResponseData_NodeOn_CorruptedData()
        throws InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, IPMIException
    {
        GetAcpiPowerState getAcpiPowerState = new GetAcpiPowerState( IpmiVersion.V20, cs, AuthenticationType.RMCPPlus,
                                                                     IpmiCommandParameters.GET_ACPI_POWER_STATE_PARAM );
        IpmiMessage message = new Ipmiv20Message( null );
        // byte[0] is requester Address.
        // byte[1] contains both NetWork Function & requester Logical Unit Number
        // byte[2] is checkSum Data
        // byte[3] is Responder Address
        // byte[4] contains Sequence Number and responder Logical Unit NUmber
        // byte[5] is command
        // byte[6] is completion code
        // from byte[7] onwards it is the Payload Data
        // byte[last] is Checksum2 for payload data
        int[] rawArray = { -127, 28, 99, 32, 8, 7, 0, 0, 0, 0 };
        byte[] respDataArr = new byte[10];
        for ( int i = 0; i < rawArray.length; i++ )
        {
            respDataArr[i] = (byte) rawArray[i];
            // System.out.println(respDataArr[i]);
        }
        IpmiLanResponse payload = new IpmiLanResponse( respDataArr );
        payload.setCommand( getAcpiPowerState.getCommandCode() );
        payload.setCompletionCode( TypeConverter.intToByte( CompletionCode.Ok.getCode() ) );
        message.setPayload( payload );
        GetAcpiPowerStateResponseData responseData =
            (GetAcpiPowerStateResponseData) getAcpiPowerState.getResponseData( message );
        assertNotNull( responseData );
        assertEquals( DevicePowerState.D0, responseData.getDeviceAcpiPowerState() );
        assertEquals( SystemPowerState.S0_G0, responseData.getSystemAcpiPowerState() );
    }

    /**
     * Test GetResponseData for Nodes which are Powered Up, Gives IPMIException if the completion code was not OK
     */
    @Test( expected = IPMIException.class )
    public void testGetResponseData_NodeOn_NoCompletionCode()
        throws InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, IPMIException
    {
        GetAcpiPowerState getAcpiPowerState = new GetAcpiPowerState( IpmiVersion.V20, cs, AuthenticationType.RMCPPlus,
                                                                     IpmiCommandParameters.GET_ACPI_POWER_STATE_PARAM );
        IpmiMessage message = new Ipmiv20Message( null );
        // byte[0] is requester Address.
        // byte[1] contains both NetWork Function & requester Logical Unit Number
        // byte[2] is checkSum Data
        // byte[3] is Responder Address
        // byte[4] contains Sequence Number and responder Logical Unit NUmber
        // byte[5] is command
        // byte[6] is completion code
        // from byte[7] onwards it is the Payload Data
        // byte[last] is Checksum2 for payload data
        int[] rawArray = { -127, 28, 99, 32, 8, 7, 0, 0, 0, -47 };
        byte[] respDataArr = new byte[10];
        for ( int i = 0; i < rawArray.length; i++ )
        {
            respDataArr[i] = (byte) rawArray[i];
            // System.out.println(respDataArr[i]);
        }
        IpmiLanResponse payload = new IpmiLanResponse( respDataArr );
        payload.setCommand( getAcpiPowerState.getCommandCode() );
        payload.setCompletionCode( TypeConverter.intToByte( CompletionCode.UnspecifiedError.getCode() ) );
        message.setPayload( payload );
        GetAcpiPowerStateResponseData responseData =
            (GetAcpiPowerStateResponseData) getAcpiPowerState.getResponseData( message );
        assertNotNull( responseData );
        assertEquals( DevicePowerState.D0, responseData.getDeviceAcpiPowerState() );
        assertEquals( SystemPowerState.S0_G0, responseData.getSystemAcpiPowerState() );
    }
}
