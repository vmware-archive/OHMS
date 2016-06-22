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
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.veraxsystems.vxipmi.coding.protocol.IpmiMessage;
import com.veraxsystems.vxipmi.coding.protocol.Ipmiv20Message;
import com.veraxsystems.vxipmi.coding.security.CipherSuite;
import com.veraxsystems.vxipmi.common.TypeConverter;
import com.vmware.vrack.coding.commands.application.SetUserPasswordCommand;
import com.vmware.vrack.coding.commands.application.SetUserPasswordCommandResponseData;
import com.vmware.vrack.coding.commands.application.UserPasswordOperation;

/**
 * Test class for Set User Password Command
 * 
 * @author Yagnesh Chawda
 */
public class SetUserPasswordCommandTest
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
        // PropertiesManager.getInstance().setProperty("timeout", "30000");
        connector = new IpmiConnector( PORT );
        handle = connector.createConnection( InetAddress.getByName( "10.28.197.202" ) );
        cs = connector.getAvailableCipherSuites( handle ).get( 3 );
        connector.getChannelAuthenticationCapabilities( handle, cs, PrivilegeLevel.Administrator );
        connector.openSession( handle, "vmware", "ca$hc0w", null );
        System.out.println( "Channel authentication capabilities received" );
        // connector.openSession(handle, "ADMIN", "ADMIN", null);
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
     * Test for GetResponseData function
     * 
     * @throws InvalidKeyException
     * @throws IllegalArgumentException
     * @throws NoSuchAlgorithmException
     * @throws IPMIException
     */
    @Test
    public void testGetResponseData()
        throws InvalidKeyException, IllegalArgumentException, NoSuchAlgorithmException, IPMIException
    {
        byte[] preparedParameter =
            SetUserPasswordCommand.preparePasswordParameter( 6, IpmiVersion.V20, UserPasswordOperation.Set_Password,
                                                             "ADMIN" );
        SetUserPasswordCommand setUserPasswordCommand =
            new SetUserPasswordCommand( IpmiVersion.V20, cs, AuthenticationType.RMCPPlus, preparedParameter );
        IpmiMessage message = new Ipmiv20Message( null );
        // byte[0] is requester Address.
        // byte[1] contains both NetWork Function & requester Logical Unit Number
        // byte[2] is checkSum Data
        // byte[3] is Responder Address
        // byte[4] contains Sequence Number and responder Logical Unit NUmber
        // byte[5] is command
        // byte[6] is completion code
        // from byte[7] - byte[lastbyte-1] onwards it is the Payload Data
        // byte[last] is the checksum2 for byte[3] to byte[last-1]
        int[] rawArray = { -127, 28, 99, 32, 8, 71, 0, -111 };
        byte[] respDataArr = new byte[8];
        for ( int i = 0; i < rawArray.length; i++ )
        {
            respDataArr[i] = (byte) rawArray[i];
            // System.out.println(respDataArr[i]);
        }
        IpmiLanResponse payload = new IpmiLanResponse( respDataArr );
        payload.setCommand( setUserPasswordCommand.getCommandCode() );
        if ( respDataArr[6] == 0 )
        {
            payload.setCompletionCode( TypeConverter.intToByte( CompletionCode.Ok.getCode() ) );
        }
        message.setPayload( payload );
        SetUserPasswordCommandResponseData responseData =
            (SetUserPasswordCommandResponseData) setUserPasswordCommand.getResponseData( message );
        assertNotNull( responseData );
        assertEquals( CompletionCode.Ok, payload.getCompletionCode() );
    }

    @Test
    public void testPreparePasswordParameter()
    {
    }
}
