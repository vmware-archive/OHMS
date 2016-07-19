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
 * Wrapper class Get Channel Info of BMC request.
 * 
 * @author Vmware
 */
public class GetChannelInfoCommand
    extends IpmiCommandCoder
{
    private byte[] parameter = new byte[1];

    /**
     * Initiates GetChannelInfoCommand for encoding and decoding. Here parameter is the number of channel of which info
     * is being retrieived
     *
     * @param version - IPMI version of the command.
     * @param cipherSuite - {@link CipherSuite} containing authentication, confidentiality and integrity algorithms for
     *            this session.
     * @param authenticationType - Type of authentication used. Must be RMCPPlus for IPMI v2.0.
     * @param channel - number of the channel of which its info is being retrieved
     */
    public GetChannelInfoCommand( IpmiVersion version, CipherSuite cipherSuite, AuthenticationType authenticationType,
                                  byte channel )
    {
        super( version, cipherSuite, authenticationType );
        /* Make sure the most significant 4 bit are zeros */
        this.parameter[0] = TypeConverter.intToByte( channel & TypeConverter.intToByte( 0xf ) );
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
     * GetCommandCode to return the Custom command Here Get Channel Info command
     */
    @Override
    public byte getCommandCode()
    {
        return IpmiCommandCodes.GET_CHANNEL_INFO;
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
        GetChannelInfoCommandResponseData responseData = new GetChannelInfoCommandResponseData();
        responseData.setChannelNumber( raw[0] & TypeConverter.intToByte( 0xf ) );
        responseData.setChannelMedium( raw[1] & TypeConverter.intToByte( 0x3f ) );
        responseData.setChannelProtocol( raw[2] & TypeConverter.intToByte( 0x1f ) );
        responseData.setSessionSupport( ( raw[3] & TypeConverter.intToByte( 0xc0 ) ) >> 6 );
        responseData.setActiveSessions( raw[3] & TypeConverter.intToByte( 0x3f ) );
        responseData.setVendorId( TypeConverter.byteToInt( raw[6] ) << 16 | TypeConverter.byteToInt( raw[5] ) << 8
            | TypeConverter.byteToInt( raw[4] ) );
        return responseData;
    }
}
