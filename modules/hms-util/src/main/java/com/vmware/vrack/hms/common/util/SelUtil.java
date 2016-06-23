/* ********************************************************************************
 * SelUtil.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import com.vmware.vrack.hms.common.resource.fru.SensorType;
import com.vmware.vrack.hms.common.resource.sel.ReadingType;
import com.vmware.vrack.hms.common.resource.sel.SelRecord;

public class SelUtil
{
    public synchronized static SelRecord compareSelRecord( SelRecord record, SelRecord selFilter )
    {
        if ( record != null )
        {
            if ( selFilter != null )
            {
                SensorType sensorTypeFilter = selFilter.getSensorType();
                if ( sensorTypeFilter == null
                    || ( sensorTypeFilter != null && sensorTypeFilter == record.getSensorType() ) )
                {
                    ReadingType eventFilter = selFilter.getEvent();
                    if ( eventFilter == null || ( eventFilter != null && eventFilter == record.getEvent() ) )
                    {
                        return record;
                    }
                }
                // If selFilter contains field that doesnot match with the SelRecord, return null
                return null;
            }
        }
        return record;
    }
}
