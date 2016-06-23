/* ********************************************************************************
 * ServerItemBoardInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.boardvendorservice.resource;

/**
 * This will hold the Model Name and Manufacturer of the Board
 * 
 * @author VMware, Inc.
 */
public class ServerItemBoardInfo
{
    private String manufacturer;

    private String model;

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer( String manufacturer )
    {
        this.manufacturer = manufacturer;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel( String model )
    {
        this.model = model;
    }
}
