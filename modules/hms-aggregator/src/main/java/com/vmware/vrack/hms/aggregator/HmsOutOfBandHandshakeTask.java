/* ********************************************************************************
 * HmsOutOfBandHandshakeTask.java
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
package com.vmware.vrack.hms.aggregator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.NetworkInterfaceUtil;
import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;

/**
 * This class responsible for initiating Handshake with OOB during Boot-up.
 *
 * @author spolepalli
 */
@Component
public class HmsOutOfBandHandshakeTask
{

    @Value( "${hms.aggregator.network.interface.name:eth1}" )
    private String hmsAggregatorNetworkInterfaceName;

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger( HmsOutOfBandHandshakeTask.class );

    /**
     * Method responsible for making handshake with Out of Band. Will pass the VRM Ip to Out of Band which will be used
     * by the same for communication from Out of band to Aggregator
     *
     * @param aggregatorIp
     * @return
     * @throws HmsException
     */
    public boolean init( String aggregatorIp, String source )
        throws HmsException
    {
        try
        {
            String serverAddress = aggregatorIp;

            if ( StringUtils.isBlank( aggregatorIp ) )
            {
                serverAddress = NetworkInterfaceUtil.getByInterfaceName( hmsAggregatorNetworkInterfaceName );
                LOG.info( "aggreagtor Ip: {} ", serverAddress );

                if ( StringUtils.isBlank( serverAddress ) )
                {
                    throw new HmsException( "Aggregator ip found to be blank" );
                }
            }

            HmsOobAgentRestTemplate<String> restTemplate = new HmsOobAgentRestTemplate<String>( serverAddress );
            String path = Constants.HMS_OOB_HANDSHAKE + "/" + serverAddress + "/" + source;

            ResponseEntity<Object> response = restTemplate.exchange( HttpMethod.POST, path, Object.class );
            if ( response != null && response.getStatusCode() == HttpStatus.OK )
            {
                LOG.debug( "Out of band handshake ends" );
                return true;
            }
            else
            {
                String exception =
                    String.format( "unsuccessful error code returned from oob agent during handshake, reponse: %s",
                                   response );
                LOG.error( exception );
                throw new HmsException( exception );
            }
        }
        catch ( Throwable e )
        {
            String exception = String.format( "Exception occured during handshake with oob agent: %s", e );
            LOG.error( exception );
            throw new HmsException( exception, e );
        }
    }
}
