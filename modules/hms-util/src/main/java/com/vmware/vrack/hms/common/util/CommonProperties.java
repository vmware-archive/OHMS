/* ********************************************************************************
 * CommonProperties.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommonProperties
{
    /**
     * Username required to connect PRM with basic authentication
     */
    private static String prmBasicAuthUser;

    /**
     * Password required to connect PRM with basic authentication
     */
    private static String prmBasicAuthPass;

    /**
     * Thread pool count.
     */
    private static Integer pluginThreadPoolCount;

    /**
     * Plugin Task Timeout
     */
    private static Long pluginTaskTimeOut;

    @Autowired
    @Value( "${prm.basic.username}" )
    public void setPrmBasicAuthUser( String prmBasicAuthUser )
    {
        CommonProperties.prmBasicAuthUser = prmBasicAuthUser;
    }

    @Autowired
    @Value( "${prm.basic.password}" )
    public void setPrmBasicAuthPass( String prmBasicAuthPass )
    {
        CommonProperties.prmBasicAuthPass = prmBasicAuthPass;
    }

    public static String getPrmBasicAuthUser()
    {
        return prmBasicAuthUser;
    }

    public static String getPrmBasicAuthPass()
    {
        return prmBasicAuthPass;
    }

    public static Integer getPluginThreadPoolCount()
    {
        return pluginThreadPoolCount;
    }

    @Value( "${hms.task.scheduler.thread.count}" )
    public void setPluginThreadPoolCount( Integer pluginThreadPoolCount )
    {
        CommonProperties.pluginThreadPoolCount = pluginThreadPoolCount;
    }

    public static Long getPluginTaskTimeOut()
    {
        return pluginTaskTimeOut;
    }

    @Value( "${hms.plugin.task.timeout}" )
    public void setPluginTaskTimeOut( Long pluginTaskTimeOut )
    {
        CommonProperties.pluginTaskTimeOut = pluginTaskTimeOut;
    }
}
