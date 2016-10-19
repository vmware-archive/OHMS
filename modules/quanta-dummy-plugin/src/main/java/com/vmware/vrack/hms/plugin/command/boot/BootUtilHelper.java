package com.vmware.vrack.hms.plugin.command.boot;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.BiosBootType;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceSelector;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceType;
import com.vmware.vrack.hms.common.resource.chassis.BootOptionsValidity;
import com.vmware.vrack.hms.plugin.command.Command;
import com.vmware.vrack.hms.plugin.ipmicode.BmcConstants;
import com.vmware.vrack.hms.plugin.ipmicode.BootOptionParameters;
import com.vmware.vrack.hms.plugin.ipmicode.NetFunction;

public class BootUtilHelper
{

    private static Logger logger = Logger.getLogger( BootUtilHelper.class );

    private static final Pattern bootOptionsEndMessage = Pattern.compile( "ipmiutil cmd, completed successfully" ),
                    bootOptionsResponse = Pattern.compile( "(respData\\[len=\\d+\\]:)((\\s\\w+)*)" );

    public static boolean setBootOptions( ServiceHmsNode serviceHmsNode, SystemBootOptions systemBootOptions,
                                          String command )
        throws IOException, HmsException
    {

        String commandStr = command + " cmd" + " -N " + serviceHmsNode.getManagementIp() + " -U "
            + serviceHmsNode.getManagementUserName() + " -P " + serviceHmsNode.getManagementUserPassword()
            + " -J 3 -V 4 ";

        /*
         * Adding bus number (usually 00), resource slave address (usually 0x20), NetFunction code and command code to
         * the command string
         */
        commandStr += BmcConstants.bus + " " + BmcConstants.rsSa + " " + NetFunction.CHASSIS_Request + " "
            + BootOptionParameters.SET_SYSTEM_BOOT_OPTIONS_COMMAND;

        // Sample Boot parameter looks like following
        // byte[] bootParameter = {0x05, 0x00, 0x04, 0x00, 0x00, 0x00};
        byte[] bootParameterB = getByteArrayFromSystemBootOptions( systemBootOptions );

        String bootParameterS = convertByteArraytoHex( bootParameterB );

        commandStr += bootParameterS;

        BufferedReader br = Command.executeCommand( commandStr );
        String line;
        while ( ( line = br.readLine() ) != null )
        {
            Matcher matcher = bootOptionsEndMessage.matcher( line );
            if ( matcher.find() )
                return true;
        }
        throw new HmsException( "setBootOptions command execution got failed !" );
    }

    private static byte[] getByteArrayFromSystemBootOptions( SystemBootOptions systemBootOptions )
    {
        byte[] data = new byte[6];
        if ( systemBootOptions != null )
        {
            try
            {
                data[0] = BootOptionParameters.BOOT_FLAG_SELECTOR;
                if ( systemBootOptions.getBootFlagsValid() != null )
                {
                    // Setting the BootFlagsValid Bit (the 7th bit), set if true, un-set if false
                    data[1] = (byte) ( systemBootOptions.getBootFlagsValid() ? ( data[1] | ( 1 << 7 ) )
                                    : data[1] & ~( 1 << 7 ) );

                    // Setting the applicableForNextBootOnly Bit (6th bit), set bit to 0 if true
                    data[1] =
                        (byte) ( ( systemBootOptions.getBootOptionsValidity() == BootOptionsValidity.NextBootOnly )
                                        ? data[1] & ~( 1 << 6 ) : ( data[1] | ( 1 << 6 ) ) );

                    // Setting the BiosBootType , either Legacy or EFI boot
                    data[1] = (byte) ( ( systemBootOptions.getBiosBootType() == BiosBootType.EFI )
                                    ? ( data[1] | ( 1 << 5 ) ) : data[1] & ~( 1 << 5 ) );
                }

                if ( systemBootOptions.getBootDeviceSelector() != null )
                {
                    // Setting the Boot Device Selector
                    data[2] = systemBootOptions.getBootDeviceSelector().getCode();
                }

                // Not used so leaving it as 0
                data[3] = (byte) 0x00;
                data[4] = (byte) 0x00;

                if ( systemBootOptions.getBootDeviceInstanceNumber() != null
                    && systemBootOptions.getBootDeviceInstanceNumber() <= 15
                    && systemBootOptions.getBootDeviceType() != null )
                {
                    data[5] = systemBootOptions.getBootDeviceInstanceNumber().byteValue();

                    // Setting the Boot Device Type as internal or External
                    data[5] = (byte) ( ( systemBootOptions.getBootDeviceType() == BootDeviceType.Internal )
                                    ? ( data[5] | ( 1 << 4 ) ) : data[5] & ~( 1 << 4 ) );
                }
            }
            catch ( Exception e )
            {
                logger.error( "Error While Converting From SystemBootOptions Object to Byte Array: " + e );
            }
        }
        else
        {
            logger.error( "Unable to convert from SystemBootOptions Object to Byte Array because SystemBootOptions was null." );
        }

        return data;
    }

    public static String convertByteArraytoHex( byte[] bootParameterB )
    {
        String byteToHexTemp = DatatypeConverter.printHexBinary( bootParameterB );
        String byteToHex = "";
        for ( int i = 0; i < bootParameterB.length; i++ )
        {
            byteToHex += " " + byteToHexTemp.substring( 2 * i, 2 * ( i + 1 ) );
        }
        return byteToHex;
    }

    public static SystemBootOptions getBootOptions( ServiceHmsNode serviceHmsNode, String command )
        throws IOException, HmsException
    {
        boolean success = false;
        String response = "";
        String commandStr = command + " cmd" + " -N " + serviceHmsNode.getManagementIp() + " -U "
            + serviceHmsNode.getManagementUserName() + " -P " + serviceHmsNode.getManagementUserPassword()
            + " -J 3 -V 4 ";

        /*
         * Adding bus number (usually 00), resource slave address (usually 0x20), NetFunction code and command code to
         * the command string
         */

        commandStr += BmcConstants.bus + " " + BmcConstants.rsSa + " " + NetFunction.CHASSIS_Request + " "
            + BootOptionParameters.GET_SYSTEM_BOOT_OPTIONS_COMMAND;

        // Adding Boot options parameters
        commandStr += " " + BootOptionParameters.GET_BOOT_OPTIONS_PARAMETERS;

        BufferedReader br = Command.executeCommand( commandStr );
        String line;
        while ( ( line = br.readLine() ) != null )
        {
            Matcher matcher = bootOptionsEndMessage.matcher( line );
            if ( matcher.find() )
                success = true;
            matcher = bootOptionsResponse.matcher( line );
            if ( matcher.find() )
                response = matcher.group( 2 );
        }

        if ( success )
        {
            byte[] bootResponseInBytes = convertHexToByteArray( response );

            try
            {
                return getSystemBootOptionsFromByteArray( bootResponseInBytes );
            }
            catch ( HmsException e )
            {
                throw e;
            }
        }

        else
        {
            String err = "Unable to execute the getBootOptions command successfully on the BMC";
            throw new HmsException( err );
        }
    }

    private static byte[] convertHexToByteArray( String response )
    {
        response = response.replaceAll( " ", "" );
        byte[] result = DatatypeConverter.parseHexBinary( response );
        return result;
    }

    private static SystemBootOptions getSystemBootOptionsFromByteArray( byte[] bootResponse )
        throws HmsException
    {
        SystemBootOptions systemBootOption = new SystemBootOptions();
        if ( ( bootResponse[0] | (byte) 0 ) == 1 )
        {
            systemBootOption.setBootFlagsValid( ( bootResponse[2] & ( 1 << 7 ) ) == 0x80 ? true : false );
            systemBootOption.setBootOptionsValidity( ( bootResponse[2] & ( 1 << 6 ) ) == 0x40
                            ? BootOptionsValidity.Persistent
                            : BootOptionsValidity.NextBootOnly );
            systemBootOption.setBiosBootType( ( bootResponse[2] & ( 1 << 5 ) ) == 0x20 ? BiosBootType.EFI
                            : BiosBootType.Legacy );

            systemBootOption.setBootDeviceSelector( BootDeviceSelector.getBootDeviceSelector( (byte) ( bootResponse[3]
                & 0x3c ) ) );
            
            systemBootOption.setBootDeviceType( ( bootResponse[6] & ( 1 << 4 ) ) == 0x10 ? BootDeviceType.Internal
                            : BootDeviceType.External );

            systemBootOption.setBootDeviceInstanceNumber( bootResponse[6] & 0x0f );

            return systemBootOption;
        }
        else
        {
            String err = "Error in getting System Boot Options from the hexadecimal response.";
            throw new HmsException( err );
        }
    }
}
