/* ********************************************************************************
 * BoardInfo.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
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
