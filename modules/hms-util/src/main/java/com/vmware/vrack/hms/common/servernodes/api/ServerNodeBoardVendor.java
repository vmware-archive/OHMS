/* ********************************************************************************
 * ServerNodeBoardVendor.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api;

/**
 * Board Manufacturers List
 * 
 * @author Yagnesh Chawda
 */
public enum ServerNodeBoardVendor
{
    INTEL( ServerNodeBoardVendor.INTEL_CORP ),
    SUPERMICRO( ServerNodeBoardVendor.SUPERMICRO_CORP ),
    QUANTA( ServerNodeBoardVendor.QUANTA_CORP ),
    OTHERS( ServerNodeBoardVendor.OTHER_CORP );
    private static final String INTEL_CORP = "Intel Corporation";

    private static final String SUPERMICRO_CORP = "Supermicro";

    private static final String QUANTA_CORP = "Quanta";

    private static final String OTHER_CORP = "Others";

    private String vendorName;

    private ServerNodeBoardVendor( String name )
    {
        vendorName = name;
    }

    public String getVendorName()
    {
        return vendorName;
    }

    /**
     * @param vendorFullName
     * @return
     */
    public static ServerNodeBoardVendor getServerNodeBoardVendor( String vendorFullName )
    {
        if ( INTEL_CORP.equals( vendorFullName ) )
        {
            return ServerNodeBoardVendor.INTEL;
        }
        else if ( SUPERMICRO_CORP.equals( vendorFullName ) )
        {
            return ServerNodeBoardVendor.SUPERMICRO;
        }
        else if ( QUANTA_CORP.equals( vendorFullName ) )
        {
            return ServerNodeBoardVendor.QUANTA;
        }
        else if ( OTHER_CORP.equals( vendorFullName ) )
        {
            return ServerNodeBoardVendor.OTHERS;
        }
        else
        {
            return ServerNodeBoardVendor.OTHERS;
        }
    }
}
