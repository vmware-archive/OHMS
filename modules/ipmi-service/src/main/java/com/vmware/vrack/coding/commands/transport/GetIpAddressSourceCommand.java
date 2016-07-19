package com.vmware.vrack.coding.commands.transport;

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
 * Wrapper class Get IP Address Source of BMC request.
 * 
 * @author Yagnsesh Chawda
 */
public class GetIpAddressSourceCommand
    extends IpmiCommandCoder
{
    private byte[] parameter;

    /**
     * Initiates GetIpAddressSourceCommand for encoding and decoding. Here parameter variable expects byte array of size
     * 4. 1st byte - Channel number like 0x01. IF the most significant bit is 1 then it will fetch parameter revision
     * ONLY 2nd byte - Parameter selector number . refer Ipmi Specification. 3rd byte - Set Selector. If not implemented
     * set as 0x00 4th byte - block selector. if not implemented set as 0x00
     *
     * @param version - IPMI version of the command.
     * @param cipherSuite - {@link CipherSuite} containing authentication, confidentiality and integrity algorithms for
     *            this session.
     * @param authenticationType - Type of authentication used. Must be RMCPPlus for IPMI v2.0.
     */
    public GetIpAddressSourceCommand( IpmiVersion version, CipherSuite cipherSuite,
                                      AuthenticationType authenticationType, byte[] parameter )
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
     * GetCommandCode to return the Custom command Here Get Ip Address Source is a parameter function of Get LAN
     * Configuration
     */
    @Override
    public byte getCommandCode()
    {
        return IpmiCommandCodes.GET_LAN_CONFIG_PARAMETERS;
    }

    @Override
    public NetworkFunction getNetworkFunction()
    {
        return NetworkFunction.TransportRequest;
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
        GetIpAddressSourceCommandResponseData responseData = new GetIpAddressSourceCommandResponseData();
        responseData.setParameterRevision( raw[0] );
        // If the parameter[0] 's MS bit is not set to fetch only parameter revision only i.e 1
        // If parameter[0]'s MS bit is 0 then only it will fetch IP Address Source
        if ( ( parameter != null ) && ( ( parameter[0] & TypeConverter.intToByte( 0x80 ) ) == 0 ) )
        {
            responseData.setIpAddressSourceCode( raw[1] );
        }
        return responseData;
    }
}
