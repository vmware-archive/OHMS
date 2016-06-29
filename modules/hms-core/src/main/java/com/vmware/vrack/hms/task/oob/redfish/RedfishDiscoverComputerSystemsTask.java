/*
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.vmware.vrack.hms.task.oob.redfish;

import com.vmware.vrack.hms.common.ExternalService;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.boardvendorservice.api.IRedfishService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.configuration.ServiceItem;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsResourceBusyException;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.vmware.vrack.hms.boardservice.BoardServiceProvider.getServiceForExternalService;
import static com.vmware.vrack.hms.boardservice.HmsPluginServiceCallWrapper.invokeHmsPluginExternalServiceService;
import static java.lang.String.format;

public class RedfishDiscoverComputerSystemsTask
    extends RedfishTask
{
    private static Logger logger = Logger.getLogger( RedfishDiscoverComputerSystemsTask.class );

    private List<ServerNode> discoveredNodes = new ArrayList<>();

    public List<ServerNode> getDiscoveredNodes()
    {
        return discoveredNodes;
    }

    @Override
    public void executeTask()
        throws Exception
    {
        String serviceEndpoint = this.getExternalService().getServiceEndpoint();
        logger.debug( "RedfishGetComputerSystemsTask called for: " + serviceEndpoint );
        try
        {
            final ExternalService externalService = this.getExternalService();
            IBoardService boardService = getServiceForExternalService( externalService );
            if ( boardService != null )
            {
                if ( boardService instanceof IRedfishService )
                {
                    ServiceItem serviceItem = new ServiceItem();
                    serviceItem.setServiceEndpoint( externalService.getServiceEndpoint() );
                    serviceItem.setServiceType( externalService.getServiceType() );
                    Object[] paramsArray = new Object[] { URI.create( serviceItem.getServiceEndpoint() ) };

                    List<ServiceHmsNode> nodes = invokeHmsPluginExternalServiceService( boardService,
                                                                                        externalService,
                                                                                        "getNodesForComputerSystems",
                                                                                        paramsArray );

                    for ( ServiceHmsNode hmsServiceNode : nodes )
                    {
                        ServerNode node = new ServerNode();
                        node.setNodeID( hmsServiceNode.getNodeID() );
                        node.setUuid( hmsServiceNode.getUuid() );
                        node.setBoardVendor( externalService.getServiceType() );
                        node.setBoardProductName( externalService.getServiceType() );
                        discoveredNodes.add( node );
                    }
                }
                else
                {
                    throw new Exception( "Assigned Board Service is not a Redfish service" );
                }
            }
            else
            {
                throw new Exception( "Board Service is NULL for Service:" + serviceEndpoint );
            }
        }
        catch ( HmsResourceBusyException e )
        {
            String error = format( "HMS Resource is Busy for the service [%s]. Please try after some time",
                                   serviceEndpoint );
            logger.debug( error, e );
            throw e;
        }
        catch ( Exception e )
        {
            logger.error( "Error while getting Computer Systems for Service:" + serviceEndpoint, e );
            throw new HmsException( "Error while getting Computer Systems for Service:" + serviceEndpoint, e );
        }
    }
}
