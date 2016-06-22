/* ********************************************************************************
 * MemoryTypeMapper.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api.memory;

/**
 * Mapper to map memory Type code obtained from CIM Client to respective Memory Type
 * 
 * @author VMware, Inc.
 */
public class MemoryTypeMapper
{
    private static final String UNKNOWN = "Unknown";

    private static final String OTHER = "Other";

    private static final String DRAM = "DRAM";

    private static final String SYNCHRONOUS_DRAM = "Synchronous_DRAM";

    private static final String CACHE_DRAM = "Cache_DRAM";

    private static final String EDO = "EDO";

    private static final String EDRAM = "EDRAM";

    private static final String VRAM = "VRAM";

    private static final String SRAM = "SRAM";

    private static final String RAM = "RAM";

    private static final String ROM = "ROM";

    private static final String FLASH = "Flash";

    private static final String EEPROM = "EEPROM";

    private static final String FEPROM = "FEPROM";

    private static final String EPROM = "EPROM";

    private static final String CDRAM = "CDRAM";

    private static final String _3DRAM = "3DRAM";

    private static final String SDRAM = "SDRAM";

    private static final String SGRAM = "SGRAM";

    private static final String RDRAM = "RDRAM";

    private static final String DDR = "DDR";

    private static final String DDR2 = "DDR-2";

    private static final String BRAM = "BRAM";

    private static final String FBDIMM = "FBDIMM";

    private static final String DMTF_RESERVED = "DMTF_Reserved";

    private static final String VENDOR_RESERVED = "Vendor_Reserved";

    public static String getMemoryType( int memoryTypeCode )
    {
        if ( memoryTypeCode == 0 )
            return UNKNOWN;
        if ( memoryTypeCode == 1 )
            return OTHER;
        if ( memoryTypeCode == 2 )
            return DRAM;
        if ( memoryTypeCode == 3 )
            return SYNCHRONOUS_DRAM;
        if ( memoryTypeCode == 4 )
            return CACHE_DRAM;
        if ( memoryTypeCode == 5 )
            return EDO;
        if ( memoryTypeCode == 6 )
            return EDRAM;
        if ( memoryTypeCode == 7 )
            return VRAM;
        if ( memoryTypeCode == 8 )
            return SRAM;
        if ( memoryTypeCode == 9 )
            return RAM;
        if ( memoryTypeCode == 10 )
            return ROM;
        if ( memoryTypeCode == 11 )
            return FLASH;
        if ( memoryTypeCode == 12 )
            return EEPROM;
        if ( memoryTypeCode == 13 )
            return FEPROM;
        if ( memoryTypeCode == 14 )
            return EPROM;
        if ( memoryTypeCode == 15 )
            return CDRAM;
        if ( memoryTypeCode == 16 )
            return _3DRAM;
        if ( memoryTypeCode == 17 )
            return SDRAM;
        if ( memoryTypeCode == 18 )
            return SGRAM;
        if ( memoryTypeCode == 19 )
            return RDRAM;
        ;
        if ( memoryTypeCode == 20 )
            return DDR;
        if ( memoryTypeCode == 21 )
            return DDR2;
        if ( memoryTypeCode == 22 )
            return BRAM;
        if ( memoryTypeCode == 23 )
            return FBDIMM;
        if ( memoryTypeCode >= 24 && memoryTypeCode <= 32567 )
            return DMTF_RESERVED;
        if ( memoryTypeCode >= 32568 && memoryTypeCode <= 65535 )
            return VENDOR_RESERVED;
        return null;
    }
}
