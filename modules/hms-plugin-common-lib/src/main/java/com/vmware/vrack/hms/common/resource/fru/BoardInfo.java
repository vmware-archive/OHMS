/* ********************************************************************************
 * BoardInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource.fru;

import java.util.Date;

/**
 * FRU record containing Board info. <br>
 * This area provides Serial Number, Part Number, and other information about the board that the FRU Information Device
 * is located on.
 */
public class BoardInfo
    extends FruRecord
{
    private String boardManufacturer;

    private String boardProductName;

    private String boardSerialNumber;

    public String getBoardManufacturer()
    {
        return boardManufacturer;
    }

    public void setBoardManufacturer( String boardManufacturer )
    {
        this.boardManufacturer = boardManufacturer;
    }

    public String getBoardProductName()
    {
        return boardProductName;
    }

    public void setBoardProductName( String boardProductName )
    {
        this.boardProductName = boardProductName;
    }

    public String getBoardSerialNumber()
    {
        return boardSerialNumber;
    }

    public void setBoardSerialNumber( String boardSerialNumber )
    {
        this.boardSerialNumber = boardSerialNumber;
    }
}
