/* ********************************************************************************
 * IBHDDInfoTask.java
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
