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
 * Wrapper class for Get User Name .
 * 
 * @author Yagnesh Chawda
 */
public class GetUserNameViaOob
    extends IpmiCommandCoder
{
    private byte[] parameter;

    /**
     * Initiates GetUserNameVia for encoding and decoding.
     *
     * @param version - IPMI version of the command.
     * @param cipherSuite - {@link CipherSuite} containing authentication, confidentiality and integrity algorithms for
     *            this session.
     * @param authenticationType - Type of authentication used. Must be RMCPPlus for IPMI v2.0.
     */
    public GetUserNameViaOob( IpmiVersion version, CipherSuite cipherSuite, AuthenticationType authenticationType,
                              byte[] param )
    {
        super( version, cipherSuite, authenticationType );
        this.parameter = param;
        if ( version == IpmiVersion.V20 && authenticationType != AuthenticationType.RMCPPlus )
        {
            throw new IllegalArgumentException( "Authentication Type must be RMCPPlus for IPMI v2.0 messages" );
        }
    }

    /**
     * preparePayload to construct the Command for sending along with the parameters here param is the parameter
     * instructing to fetch the second userName
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
        return IpmiCommandCodes.GET_USER_NAME;
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
        byte[] raw = message.getPayload().getIpmiCommandData();
        StringBuilder userName = new StringBuilder();
        for ( byte b : raw )
        {
            userName.append( (char) b );
        }
        // System.out.println("\n" + userName);
        GetUserNameViaOobResponseData responseData = new GetUserNameViaOobResponseData();
        responseData.setUserName( userName.toString().trim() );
        return responseData;
    }
}