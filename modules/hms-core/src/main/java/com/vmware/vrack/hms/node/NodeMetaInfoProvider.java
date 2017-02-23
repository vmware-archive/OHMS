/* ********************************************************************************
 * NodeMetaInfoProvider.java
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
package com.vmware.vrack.hms.node;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.util.CommonProperties;

/**
 * Class which maintains meta info about the number of concurrent operations running on a node.
 * 
 * @author vmware
 */
public class NodeMetaInfoProvider
{

    private static final Logger logger = Logger.getLogger( NodeMetaInfoProvider.class );

    private static final ReentrantLock nodeServiceLock = new ReentrantLock();

    // Map containing the number of concurrent operations running on a node <nodeId, count>
    private static final Map<String, Integer> nodeMetaInfoMap = new ConcurrentHashMap<String, Integer>();

    /**
     * Checks if the given node is already running given number of concurrent operations on the service or not, if not,
     * it will increase its count by one and returns true. Else it will return false.
     * 
     * @param nodeId
     * @return
     */
    public static boolean increaseConcurrentOperationCount( String nodeId )
    {
        // Deliberately keeping true as default, as we better allow operations, rather than completely blocking them
        // incase of some issue.
        boolean canPerformOperationOnNode = true;

        // Get the lock for determining if the operation can be performed or not on the given node
        nodeServiceLock.lock();

        try
        {
            logger.debug( "Got the lock for determining feasibility to call operation service for node:" + nodeId );

            Integer currentOperationCount = (Integer) nodeMetaInfoMap.get( nodeId );

            if ( currentOperationCount == null )
            {
                currentOperationCount = 0;
            }

            int maxConcurrentTasksPerNode = CommonProperties.getMaxConcurrentTasksPerNode();

            if ( currentOperationCount >= maxConcurrentTasksPerNode )
            {
                logger.warn( "Will not perform further operation. Reached Max concurrent operation count for Node:"
                    + nodeId + ". Current count:" + maxConcurrentTasksPerNode );
                canPerformOperationOnNode = false;
            }
            else
            {
                ++currentOperationCount;
                logger.debug( "Node can perform operation. Increasing current concurrent operation count for Node:"
                    + nodeId + " set to:" + currentOperationCount );
                nodeMetaInfoMap.put( nodeId, currentOperationCount );
            }
        }
        catch ( Exception ex )
        {
            logger.warn( "Error while checking for current operation count for Node:" + nodeId, ex );
        }
        finally
        {
            nodeServiceLock.unlock();
        }

        return canPerformOperationOnNode;
    }

    /**
     * Decreases the Node's current operationCount by one. If it actually decreases the count for the node, then only it
     * returns true, else returns false.
     * 
     * @param nodeId
     * @return
     */
    public static boolean decreaseConcurrentOperationCount( String nodeId )
    {
        boolean relasedLockForOperationOnNode = false;

        // Get the lock for determining if the operation can be performed or not on the given node
        nodeServiceLock.lock();

        try
        {
            logger.debug( "Got the lock descreasing operation count for node:" + nodeId );

            Integer currentOperationCount = (Integer) nodeMetaInfoMap.get( nodeId );

            if ( currentOperationCount == null )
            {
                logger.warn( "Strange!! Operation count for Node:" + nodeId + " is NOT present" );

            }
            else if ( currentOperationCount > 0 )
            {
                --currentOperationCount;
                logger.debug( "Decreasing concurrent operation count for Node:" + nodeId + " to:"
                    + currentOperationCount );
                nodeMetaInfoMap.put( nodeId, currentOperationCount );
                relasedLockForOperationOnNode = true;
            }
            else
            {
                logger.warn( "Not resetting Concurrent operation count for Node:" + nodeId + " as it is:"
                    + currentOperationCount );
            }
        }
        catch ( Exception ex )
        {
            logger.warn( "Error while decreasing for current operation count for Node:" + nodeId, ex );
        }
        finally
        {
            nodeServiceLock.unlock();
        }

        return relasedLockForOperationOnNode;
    }

}
