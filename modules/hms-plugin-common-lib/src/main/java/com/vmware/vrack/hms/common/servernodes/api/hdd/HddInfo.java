/* ********************************************************************************
 * HddInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api.hdd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.StatusEnum;
import com.vmware.vrack.hms.common.servernodes.api.AbstractServerComponent;

/**
 * Class for HDD/Storage related properties HddInfo has the FRU component indentifiers which helps to identify the
 * Server component HDD FRU
 *
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class HddInfo
    extends AbstractServerComponent
{
    private long diskCapacityInMB;

    private StatusEnum state;

    private String type;

    private String firmwareInfo;

    /**
     * Canonical name of the hard disk e.g "naa.55cd2e404b6483b5" Required because we wanted SMART Data for HDDs
     */
    private String name;

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public long getDiskCapacityInMB()
    {
        return diskCapacityInMB;
    }

    public void setDiskCapacityInMB( long diskCapacityInMB )
    {
        this.diskCapacityInMB = diskCapacityInMB;
    }

    public StatusEnum getState()
    {
        return state;
    }

    public void setState( StatusEnum state )
    {
        this.state = state;
    }

    @JsonIgnore
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getFirmwareInfo()
    {
        return firmwareInfo;
    }

    public void setFirmwareInfo( String firmwareInfo )
    {
        this.firmwareInfo = firmwareInfo;
    }
}
