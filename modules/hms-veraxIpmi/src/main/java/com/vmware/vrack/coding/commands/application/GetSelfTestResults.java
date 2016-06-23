package com.vmware.vrack.coding.commands.application;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
 * Wrapper class for Get Self Test Results request.
 * 
 * @author Yagnesh Chawda
 */
public class GetSelfTestResults
    extends IpmiCommandCoder
{
    private byte[] parameter;

    /**
     * Initiates GetSelfTestResults for encoding and decoding.
     *
     * @param version - IPMI version of the command.
     * @param cipherSuite - {@link CipherSuite} containing authentication, confidentiality and integrity algorithms for
     *            this session.
     * @param authenticationType - Type of authentication used. Must be RMCPPlus for IPMI v2.0.
     */
    public GetSelfTestResults( IpmiVersion version, CipherSuite cipherSuite, AuthenticationType authenticationType,
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
        return IpmiCommandCodes.GET_SELF_TEST_RESULTS;
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
        GetSelfTestResultsResponseData responseData = new GetSelfTestResultsResponseData();
        // Set the first byte as Result Code and the second as Failure code
        // In case of All test Passed successfully , the failure code will be 0x00
        responseData.setSelfTestResultCode( raw[0] );
        responseData.setSelfTestFailureCode( raw[1] );
        List<String> errors = new ArrayList<String>();
        // If All tests are not passed , and selfTestResultCode is 0x57 instead of 0x55, it will
        // try to find all the possible errors and add them to the ArrayList
        if ( !responseData.isAllTestPassed() && responseData.isCorruptedOrInaccessible() )
        {
            byte failureCode = responseData.getSelfTestFailureCode();
            if ( ( failureCode & TypeConverter.intToByte( 0x01 ) ) != 0 )
            {
                errors.add( "controller operational firmware corrupted" );
            }
            if ( ( failureCode & TypeConverter.intToByte( 0x02 ) ) != 0 )
            {
                errors.add( "controller update boot block firmware corrupted" );
            }
            if ( ( failureCode & TypeConverter.intToByte( 0x04 ) ) != 0 )
            {
                errors.add( "Internal Use Area of BMC FRU corrupted" );
            }
            if ( ( failureCode & TypeConverter.intToByte( 0x08 ) ) != 0 )
            {
                errors.add( "SDR Repository empty" );
            }
            if ( ( failureCode & TypeConverter.intToByte( 0x10 ) ) != 0 )
            {
                errors.add( "IPMB signal lines do not respond" );
            }
            if ( ( failureCode & TypeConverter.intToByte( 0x20 ) ) != 0 )
            {
                errors.add( "Cannot access BMC FRU device" );
            }
            if ( ( failureCode & TypeConverter.intToByte( 0x40 ) ) != 0 )
            {
                errors.add( "Cannot access SDR Repository" );
            }
            if ( ( failureCode & TypeConverter.intToByte( 0x80 ) ) != 0 )
            {
                errors.add( "Cannot access SEL device" );
            }
        }
        // If the error is unknown or even , it will set the following as error
        if ( responseData.isUnknownError() || ( !responseData.isAllTestPassed() && ( errors.isEmpty() ) ) )
        {
            errors.add( "Unknown Error" );
        }
        responseData.setErrors( errors );
        return responseData;
    }
}
