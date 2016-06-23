/* ********************************************************************************
 * IBCPUInfoTask.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ib.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.utils.HttpClientService;

public class IBCPUInfoTask
    extends IBRestTask
{
    private static Logger logger = Logger.getLogger( IBCPUInfoTask.class );

    public IBCPUInfoTask( TaskResponse response )
    {
        super( response );
    }

    @Override
    public void executeTask()
        throws Exception
    {
        if ( getBaseUrl() != null )
        {
            try
            {
                ServiceServerNode serviceServerNode = (ServiceServerNode) node.getServiceObject();
                ObjectMapper mapper = new ObjectMapper();
                String response = HttpClientService.getInstance().postJson( getBaseUrl() + "/cpuinfo",
                                                                            mapper.writeValueAsString( serviceServerNode ),
                                                                            true, false );
                List<CPUInfo> info =
                    mapper.readValue( response,
                                      TypeFactory.defaultInstance().constructCollectionType( ArrayList.class,
                                                                                             CPUInfo.class ) );
                this.node.setCpuInfo( info );
            }
            catch ( JsonGenerationException e )
            {
                logger.error( "Error while getting In Band CPU Info for Node:" + node.getNodeID(), e );
                throw new HmsException( "Error while getting In Band CPU Info for Node:" + node.getNodeID(), e );
            }
            catch ( JsonMappingException e )
            {
                logger.error( "Error while getting In Band CPU Info for Node:" + node.getNodeID(), e );
                throw new HmsException( "Error while getting In Band CPU Info for Node:" + node.getNodeID(), e );
            }
            catch ( IOException e )
            {
                logger.error( "Error while getting In Band CPU Info for Node:" + node.getNodeID(), e );
                throw new HmsException( "Error while getting In Band CPU Info for Node:" + node.getNodeID(), e );
            }
            catch ( Exception e )
            {
                logger.error( "Error while getting In Band CPU Info for Node:" + node.getNodeID(), e );
                throw new HmsException( "Error while getting In Band CPU Info for Node:" + node.getNodeID(), e );
            }
        }
        else
        {
            logger.error( "HMS InBand URL not available, Error while getting In Band CPU Info for Node:"
                + node.getNodeID() );
            throw new HmsException( "HMS InBand URL not available, Error while getting In Band CPU Info for Node:"
                + node.getNodeID() );
        }
    }
}
