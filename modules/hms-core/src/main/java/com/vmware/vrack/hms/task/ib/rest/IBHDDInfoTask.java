/* ********************************************************************************
 * IBHDDInfoTask.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ib.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.utils.HttpClientService;

public class IBHDDInfoTask
    extends IBRestTask
{
    private static Logger logger = Logger.getLogger( IBHDDInfoTask.class );

    public IBHDDInfoTask( TaskResponse response )
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
                String response = HttpClientService.getInstance().postJson( getBaseUrl() + "/storageinfo",
                                                                            mapper.writeValueAsString( serviceServerNode ),
                                                                            true, false );
                List<HddInfo> info =
                    mapper.readValue( response,
                                      TypeFactory.defaultInstance().constructCollectionType( ArrayList.class,
                                                                                             HddInfo.class ) );
                this.node.setHddInfo( info );
            }
            catch ( JsonParseException e )
            {
                logger.error( "Error while getting In Band HDD Info for Node:" + node.getNodeID(), e );
                throw new HmsException( "Error while getting In Band HDD Info for Node:" + node.getNodeID(), e );
            }
            catch ( JsonMappingException e )
            {
                logger.error( "Error while getting In Band HDD Info for Node:" + node.getNodeID(), e );
                throw new HmsException( "Error while getting In Band HDD Info for Node:" + node.getNodeID(), e );
            }
            catch ( IOException e )
            {
                logger.error( "Error while getting In Band HDD Info for Node:" + node.getNodeID(), e );
                throw new HmsException( "Error while getting In Band HDD Info for Node:" + node.getNodeID(), e );
            }
            catch ( Exception e )
            {
                logger.error( "Error while getting In Band HDD Info for Node:" + node.getNodeID(), e );
                throw new HmsException( "Error while getting In Band HDD Info for Node:" + node.getNodeID(), e );
            }
        }
        else
        {
            logger.error( "HMS InBand URL not available, Error while getting In Band HDD Info for Node:"
                + node.getNodeID() );
            throw new HmsException( "HMS InBand URL not available, Error while getting In Band HDD Info for Node:"
                + node.getNodeID() );
        }
    }
}
