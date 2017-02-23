/* ********************************************************************************
 * CommonProperties.java
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
package com.vmware.vrack.hms.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component( "commonProperties" )
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
    // private static Integer pluginThreadPoolCount;

    private static Integer maxConcurrentTasksPerNode = 5;

    private static Long concurrentOperationRetryThreadSleepTime = 20000L;

    private static Long retryThreadSleepTime = 30000L;

    /**
     * Plugin Task Timeout
     */
    // private static Long pluginTaskTimeOut;

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

    public static Integer getMaxConcurrentTasksPerNode()
    {
        return maxConcurrentTasksPerNode;
    }

    @Value( "${hms.max.concurrent.tasks.per.node:5}" )
    public void setMaxConcurrentTasksPerNode( Integer maxConcurrentTasksPerNode )
    {
        CommonProperties.maxConcurrentTasksPerNode = maxConcurrentTasksPerNode;
    }

    public static Long getConcurrentOperationRetryThreadSleepTime()
    {
        return concurrentOperationRetryThreadSleepTime;
    }

    @Value( "${hms.node.concurrent.operation.retry.thread.sleep.time:20000}" )
    public void setConcurrentOperationRetryThreadSleepTime( Long concurrentOperationRetryThreadSleepTime )
    {
        CommonProperties.concurrentOperationRetryThreadSleepTime = concurrentOperationRetryThreadSleepTime;
    }

    @Value( "${sleep.before.retry.millis:30000}" )
    public void setRetryThreadSleepTime( Long retryThreadSleepTime )
    {
        CommonProperties.retryThreadSleepTime = retryThreadSleepTime;
    }

    public static Long getRetryThreadSleepTime()
    {
        return retryThreadSleepTime;
    }

    /*
     * public static Integer getPluginThreadPoolCount() { return pluginThreadPoolCount; }
     * @Value("${hms.task.scheduler.thread.count}") public void setPluginThreadPoolCount(Integer pluginThreadPoolCount)
     * { CommonProperties.pluginThreadPoolCount = pluginThreadPoolCount; } public static Long getPluginTaskTimeOut() {
     * return pluginTaskTimeOut; }
     * @Value("${hms.plugin.task.timeout}") public void setPluginTaskTimeOut(Long pluginTaskTimeOut) {
     * CommonProperties.pluginTaskTimeOut = pluginTaskTimeOut; }
     */

}
