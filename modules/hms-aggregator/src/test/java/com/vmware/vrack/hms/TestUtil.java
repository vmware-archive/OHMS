/* ********************************************************************************
 * TestUtil.java
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
package com.vmware.vrack.hms;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

public class TestUtil
{

    private TestUtil()
    {
        throw new AssertionError( "Instance not required. TestUtil is a utility class." );
    }

    /**
     * Gets the server node.
     *
     * @param nodeId the node id
     * @return the server node
     */
    public static ServerNode getServerNode( final String nodeId )
    {
        ServerNode serverNode = new ServerNode();
        serverNode.setNodeID( nodeId );
        serverNode.setIbIpAddress( "ibIpAddress" );
        serverNode.setOsUserName( "osUserName" );
        serverNode.setOsPassword( "osPassword" );
        return serverNode;
    }

    /**
     * Gets the date time stamp.
     *
     * @return the date time stamp
     */
    public static String getDateTimeStamp()
    {
        Calendar cal = Calendar.getInstance();
        return Long.toString( cal.getTimeInMillis() );
    }

    /**
     * Gets the node map.
     *
     * @param serverNode the server node
     * @return the node map
     */
    public static Map<String, ServerNode> getNodeMap( ServerNode serverNode )
    {
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        nodeMap.put( serverNode.getNodeID(), serverNode );
        return nodeMap;
    }

    /**
     * Gets the value as string.
     *
     * @param object the object
     * @return the value as string
     */
    public static String getValueAsString( Object object )
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString( object );
        }
        catch ( IOException e )
        {
        }
        return null;
    }

    /**
     * Gets the temporary directory.
     *
     * @return the temporary directory
     */
    public static String getTemporaryDirectory()
    {
        return FilenameUtils.concat( System.getProperty( "java.io.tmpdir" ), TestUtil.getDateTimeStamp() );
    }
}
