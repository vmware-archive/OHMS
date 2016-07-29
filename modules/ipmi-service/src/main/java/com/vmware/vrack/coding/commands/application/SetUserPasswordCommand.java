package com.vmware.vrack.coding.commands.application;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import com.veraxsystems.vxipmi.coding.commands.IpmiCommandCoder;
import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.ResponseData;
import com.veraxsystems.vxipmi.coding.payload.CompletionCode;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.payload.lan.IpmiLanMessage;
import com.veraxsystems.vxipmi.coding.payload.lan.IpmiLanRequest;
import com.veraxsystems.vxipmi.coding.payload.lan.IpmiLanResponse;
import com.veraxsystems.vxipmi.coding.payload.lan.NetworkFunction;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.veraxsystems.vxipmi.coding.protocol.IpmiMessage;
import com.veraxsystems.vxipmi.coding.security.CipherSuite;
import com.veraxsystems.vxipmi.common.TypeConverter;
import com.vmware.vrack.coding.commands.IpmiCommandCodes;

/**
 * Wrapper class for Set User Password Command request.
 * 
 * @author Yagnesh Chawda
 */
public class SetUserPasswordCommand
    extends IpmiCommandCoder
{
    private byte[] parameter;

    /**
     * Initiates SetUserPasswordCommand for encoding and decoding.
     *
     * @param version - IPMI version of the command.
     * @param cipherSuite - {@link CipherSuite} containing authentication, confidentiality and integrity algorithms for
     *            this session.
     * @param authenticationType - Type of authentication used. Must be RMCPPlus for IPMI v2.0.
     */
    public SetUserPasswordCommand( IpmiVersion version, CipherSuite cipherSuite, AuthenticationType authenticationType,
                                   byte[] parameter )
    {
        super( version, cipherSuite, authenticationType );
        this.parameter = parameter;
        if ( version == IpmiVersion.V20 && authenticationType != AuthenticationType.RMCPPlus )
        {
            throw new IllegalArgumentException( "Authentication Type must be RMCPPlus for IPMI v2.0 messages" );
        }
    }

    /**
     * preparePayload to construct the Command for sending along with the parameters
     */
    @Override
    protected IpmiLanMessage preparePayload( int sequenceNumber )
    {
        return new IpmiLanRequest( getNetworkFunction(), getCommandCode(), parameter,
                                   TypeConverter.intToByte( sequenceNumber % 64 ) );
    }

    /**
     * GetCommandCode to return the Custom command
     */
    @Override
    public byte getCommandCode()
    {
        return IpmiCommandCodes.SET_USER_PASSWORD;
    }

    @Override
    public NetworkFunction getNetworkFunction()
    {
        return NetworkFunction.ApplicationRequest;
    }

    @Override
    public ResponseData getResponseData( IpmiMessage message )
        throws IllegalArgumentException, IPMIException, NoSuchAlgorithmException, InvalidKeyException
    {
        if ( !isCommandResponse( message ) )
        {
            throw new IllegalArgumentException( "This is not a response for your command" );
        }
        if ( !( message.getPayload() instanceof IpmiLanResponse ) )
        {
            throw new IllegalArgumentException( "Invalid response payload" );
        }
        if ( ( (IpmiLanResponse) message.getPayload() ).getCompletionCode() != CompletionCode.Ok )
        {
            throw new IPMIException( ( (IpmiLanResponse) message.getPayload() ).getCompletionCode() );
        }
        SetUserPasswordCommandResponseData responseData = new SetUserPasswordCommandResponseData();
        return responseData;
    }

    /**
     * Utility Function to prepare Parameters for Set User Password for any given User ID
     * 
     * @param userId
     * @param userPasswordOperation
     * @param passwordString
     * @return
     */
    public static byte[] preparePasswordParameter( int userId, IpmiVersion ipmiVersion,
                                                   UserPasswordOperation userPasswordOperation, String passwordString )
    {
        byte[] preparedPasswordParam = null;
        byte[] passwordInAscii = null;
        // Since max users can be 63 on any Bmc , check if the entered userId is Valid
        if ( ( userId < 64 ) && ( userId != 0 || userId != 1 ) && ipmiVersion != null && userPasswordOperation != null
            && passwordString != null && passwordString.length() <= 20 )
        {
            if ( ipmiVersion == IpmiVersion.V20 )
            {
                preparedPasswordParam = new byte[22];
                passwordInAscii = new byte[20];
                // the MS bit of this byte represents if the password is of length 20 bytes or 16 bytes
                // 20 bytes is used with IPMI ver 2 RMCP, while the 16 bytes pass is used for IPMI version 1.5
                // OR ing the user id with 0x80 will set the MS bit as 1 for 20 byte Password
                preparedPasswordParam[0] = (byte) ( userId | 0x80 );
                // for password,there should be padding 0x00 from the ending of password to the last byte
                for ( int i = 0; i < passwordString.length(); i++ )
                {
                    passwordInAscii[i] = (byte) passwordString.charAt( i );
                }
            }
            if ( ipmiVersion == IpmiVersion.V15 && passwordString.length() <= 16 )
            {
                preparedPasswordParam = new byte[18];
                passwordInAscii = new byte[16];
                // the MS bit of this byte represents if the password is of length 20 bytes or 16 bytes
                // 20 bytes is used with IPMI ver 2 RMCP, while the 16 bytes pass is used for IPMI version 1.5
                // AND ing the userId with 0x7F will set the MS bit as 0 for 16 byte password
                // 16 byte password can used with IPMI v2 and v1.5 both
                preparedPasswordParam[0] = (byte) ( userId & 0x7F );
                // for password,there should be padding 0x00 from the ending of password to the last byte
                for ( int i = 0; i < passwordString.length(); i++ )
                {
                    passwordInAscii[i] = (byte) passwordString.charAt( i );
                }
            }
            // Second byte requires to be the user Password operation
            // Test Password is used to match the password entered with the one stored on the BMC
            preparedPasswordParam[1] = userPasswordOperation.getValue();
            // Copying the Ascii converted Password to the parameter,
            System.arraycopy( passwordInAscii, 0, preparedPasswordParam, 2, passwordInAscii.length );
        }
        return preparedPasswordParam;
    }
}
