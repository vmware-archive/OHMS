/* ********************************************************************************
 * TorSwitchConfiguration.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.configuration;

import org.apache.log4j.Logger;

public class TorSwitchConfiguration
{
    private TorSwitchConfiguration()
    {
    }

    public static TorSwitchConfiguration getInstance()
    {
        if ( torSwitchConfiguration == null )
        {
            torSwitchConfiguration = new TorSwitchConfiguration();
        }
        return torSwitchConfiguration;
    }

    private static TorSwitchConfiguration torSwitchConfiguration;

    @SuppressWarnings( "unused" )
    private static Logger logger = Logger.getLogger( TorSwitchConfiguration.class );
}
