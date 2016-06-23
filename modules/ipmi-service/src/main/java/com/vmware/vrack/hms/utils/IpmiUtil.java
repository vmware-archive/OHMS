/* ********************************************************************************
 * IpmiUtil.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.utils;

import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.coding.commands.BootOptionCommandParameters;
import com.vmware.vrack.coding.commands.chassis.GetSystemBootOptionsCommandResponseData;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.BiosBootType;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceSelector;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceType;
import com.vmware.vrack.hms.common.resource.chassis.BootOptionsValidity;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;

/**
 * Utility class for Ipmi related Tasks
 * 
 * @author Vmware
 */
public class IpmiUtil
{
    private static Logger logger = Logger.getLogger( IpmiUtil.class );

    /*
     * Creates a byte array byte[6] to set system boot options using ipmi byte[0] is parameter selector byte[1] ==> -
     * 7th bit -> Boot Flag valid, 1 means Valid. - 6th bit -> 0 means applicable for next boot only, 1 means it will be
     * persistent for future boots. - 5th bit -> BIOS boot type, 0 for 'Legacy', 1 for 'EFI'. byte[2] is Boot Device
     * Selector => - bit [5:2] is the actual boot Device byte[3] is related to screen messages (Not using it right now)
     * byte[4] is related to BIOS shared Mode override and Bios Mux Control Override(Not using this byte for now)
     * byte[5] is Device Instance Selector => - 4th bit is BootDevice Type, 1 if internal, 0 if External. - bit[3:0] is
     * the Device instance number.
     */
    public static byte[] getByteArrayFromSystemBootOptions( SystemBootOptions systemBootOptions )
    {
        byte[] data = new byte[6];
        if ( systemBootOptions != null )
        {
            try
            {
                data[0] = BootOptionCommandParameters.BOOT_FLAGS_SELECTOR;
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
                    data[2] = (byte) systemBootOptions.getBootDeviceSelector().getCode();
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

    /*
     * Returns the SystemBootOptions Object from the GetSystemBootOptionsResponseData Object
     */
    public static SystemBootOptions getSystemBootOptionsFromGetSystemBootOptionsCommandResponseData( GetSystemBootOptionsCommandResponseData rd )
    {
        SystemBootOptions systemBootOptions = new SystemBootOptions();
        try
        {
            if ( rd != null )
            {
                systemBootOptions.setBootOptionsValidity( BootOptionsValidity.getBootOptionsValidity( rd.getBootOptionsValidity().getCode() ) );
                systemBootOptions.setBiosBootType( BiosBootType.getBiosBootType( rd.getBiosBootType().getCode() ) );
                systemBootOptions.setBootDeviceInstanceNumber( rd.getBootDeviceInstanceNumber() );
                systemBootOptions.setBootDeviceSelector( BootDeviceSelector.getBootDeviceSelector( rd.getBootDeviceSelector().getCode() ) );
                systemBootOptions.setBootDeviceType( BootDeviceType.getBootDeviceType( rd.getBootDeviceType().getCode() ) );
                systemBootOptions.setBootFlagsValid( rd.isBootFlagsValid() );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error when trying to get SystemBootOptions from GetSystemBootOptionsCommandResponseData :"
                + e );
        }
        return systemBootOptions;
    }

    public static byte[] getByteArrayFromChassisIdentifyOptions( ChassisIdentifyOptions chassisIdentifyOptions )
    {
        byte[] data = null;
        if ( chassisIdentifyOptions != null )
        {
            Boolean identify = chassisIdentifyOptions.getIdentify();
            Boolean forceIdentify = chassisIdentifyOptions.getForceIdentifyChassis();
            Integer interval = chassisIdentifyOptions.getInterval();
            if ( identify != null && identify )
            {
                // turn identify. first check if force identify is on. if it is not then get the interval
                if ( forceIdentify != null && forceIdentify )
                {
                    data = ( interval != null && interval <= 255 && interval > 0 )
                                    ? new byte[] { interval.byteValue(), 0x01 } : new byte[] { 0x00, 0x01 };
                    return data;
                }
                else
                {
                    data = ( interval != null && interval <= 255 && interval > 0 )
                                    ? new byte[] { interval.byteValue(), 0x00 } : null;
                    return data;
                }
            }
            else
            {
                // Turn off identify
                data = new byte[] { 0x00, 0x00 };
                return data;
            }
        }
        return data;
    }

    private static boolean compareBoardProductName( ServerNodeInfo serverNodeInfo, List<BoardInfo> boardInfos )
    {
        if ( serverNodeInfo != null && boardInfos != null )
        {
            for ( BoardInfo info : boardInfos )
            {
                if ( info.getBoardProductName() != null
                    && info.getBoardProductName().equals( serverNodeInfo.getComponentIdentifier().getProduct() ) )
                {
                    return true;
                }
            }
        }
        return false;
    }
}
