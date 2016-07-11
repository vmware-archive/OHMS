/* ********************************************************************************
 * ServerInfoTask.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ipmi;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.api.async.ConnectionHandle;
import com.veraxsystems.vxipmi.api.sync.IpmiConnector;
import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.fru.BaseUnit;
import com.veraxsystems.vxipmi.coding.commands.fru.GetFruInventoryAreaInfo;
import com.veraxsystems.vxipmi.coding.commands.fru.GetFruInventoryAreaInfoResponseData;
import com.veraxsystems.vxipmi.coding.commands.fru.ReadFruData;
import com.veraxsystems.vxipmi.coding.commands.fru.ReadFruDataResponseData;
import com.veraxsystems.vxipmi.coding.commands.fru.record.BoardInfo;
import com.veraxsystems.vxipmi.coding.commands.fru.record.FruRecord;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;

/**
 * Server Info Task to get Server Specific Information as Board Vendor, Board Model via BMC
 * 
 * @author Vmware
 */
public class ServerInfoTask
{
    private static Logger logger = Logger.getLogger( ServerInfoTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in Server Info Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    /**
     * Size of data transmitted in single ReadFru command. Different Board has, different supporting behaviour to read
     * bytes from FRU in one go. Please set right value in this field by calling setFruReadPacketSize() method in this
     * class, if you facing issue reading ServerInfo Lower values are generally safer, but it may induce some
     * performance penalty Bigger values will improve performance. If server is returning
     * "Invalid data field in Request." error during ReadFru command, FRU_READ_PACKET_SIZE should be decreased.
     */
    private int FRU_READ_PACKET_SIZE = 120;

    private final int DEFAULT_FRU_ID = 0;

    ServiceServerNode node = null;

    IpmiTaskConnector connector = null;

    ArrayList<Integer> fruList = new ArrayList<Integer>();

    public ServerInfoTask( ServiceHmsNode node, IpmiTaskConnector connector )
    {
        this( node );
        this.connector = connector;
    }

    public ServerInfoTask( ServiceHmsNode node )
    {
        this.node = (ServiceServerNode) node;
        this.fruList.add( DEFAULT_FRU_ID );
    }

    /**
     * Size of data transmitted in single ReadFru command. Different Board has, different supporting behaviour to read
     * bytes from FRU in one go. Please set right value in this field by calling setFruReadPacketSize() method in this
     * class, if you facing issue reading ServerInfo Lower values are generally safer, but it may induce some
     * performance penalty Bigger values will improve performance. If server is returning
     * "Invalid data field in Request." error during ReadFru command, FRU_READ_PACKET_SIZE should be decreased.
     * 
     * @param fruReadPacketSize
     */
    public void setFruReadPacketSize( int fruReadPacketSize )
    {
        this.FRU_READ_PACKET_SIZE = fruReadPacketSize;
    }

    public void setFruList( ArrayList<Integer> fruList )
    {
        this.fruList = fruList;
    }

    public ServerNodeInfo executeTask()
        throws Exception
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request to execute ipmi ServerInfo task for Node " + node.getNodeID() );
            ServerNodeInfo serverNodeInfo = new ServerNodeInfo();
            for ( Integer fruId : fruList )
            {
                int count = 0;
                List<FruRecord> records = processFru( connector.getConnector(), connector.getHandle(), fruId );
                for ( FruRecord record : records )
                {
                    // now we can for example display received info about board
                    if ( record instanceof BoardInfo )
                    {
                        BoardInfo bi = (BoardInfo) record;
                        ComponentIdentifier serverComponentIdentifier = new ComponentIdentifier();
                        serverComponentIdentifier.setManufacturer( bi.getBoardManufacturer() );
                        serverComponentIdentifier.setProduct( bi.getBoardProductName() );
                        serverComponentIdentifier.setPartNumber( bi.getBoardPartNumber() );
                        serverComponentIdentifier.setSerialNumber( bi.getBoardSerialNumber() );
                        serverComponentIdentifier.setManufacturingDate( bi.getMfgDate().toString() );
                        serverNodeInfo.setComponentIdentifier( serverComponentIdentifier );
                        count++;
                    }
                    if ( count > 1 )
                    {
                        break;
                    }
                }
            }
            return serverNodeInfo;
        }
        else
        {
            String err = String.format( NULL_CONNECTOR_EXCEPTION_MSG, node,
                                        ( node instanceof ServiceServerNode ) ? "" : "NOT ", connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Processes Encoded Fru Data to readable format
     * 
     * @param connector
     * @param handle
     * @param fruId
     * @return
     * @throws Exception
     */
    private List<FruRecord> processFru( IpmiConnector connector, ConnectionHandle handle, Integer fruId )
        throws Exception, IpmiServiceResponseException
    {
        List<ReadFruDataResponseData> fruData = new ArrayList<ReadFruDataResponseData>();
        logger.debug( "Received request to execute ipmi processFru for ServerInfo task for Node " + node.getNodeID() );
        try
        {
            // get the FRU Inventory Area info
            GetFruInventoryAreaInfoResponseData info =
                (GetFruInventoryAreaInfoResponseData) connector.sendMessage( handle,
                                                                             new GetFruInventoryAreaInfo( IpmiVersion.V20,
                                                                                                          handle.getCipherSuite(),
                                                                                                          AuthenticationType.RMCPPlus,
                                                                                                          fruId ) );
            int size = info.getFruInventoryAreaSize();
            BaseUnit unit = info.getFruUnit();
            // since the size of single FRU entry can exceed maximum size of the
            // message sent via IPMI, it has to be read in chunks
            for ( int i = 0; i < size; i += FRU_READ_PACKET_SIZE )
            {
                int cnt = FRU_READ_PACKET_SIZE;
                if ( i + cnt > size )
                {
                    cnt = size % FRU_READ_PACKET_SIZE;
                }
                try
                {
                    // get single package od FRU data
                    ReadFruDataResponseData data =
                        (ReadFruDataResponseData) connector.sendMessage( handle,
                                                                         new ReadFruData( IpmiVersion.V20,
                                                                                          handle.getCipherSuite(),
                                                                                          AuthenticationType.RMCPPlus,
                                                                                          fruId, unit, i, cnt ) );
                    fruData.add( data );
                }
                catch ( IPMIException e )
                {
                    logger.error( String.format( "Error while sending ReadFruData command for node [ %s ], fruId [ %s ] ",
                                                 node.getNodeID(), fruId ) );
                    logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
                    throw new IpmiServiceResponseException( e.getCompletionCode() );
                }
                catch ( Exception e )
                {
                    logger.error( String.format( "Error while sending ReadFruData command for node [ %s ], fruId [ %s ] ",
                                                 node.getNodeID(), fruId ) );
                    throw e;
                }
            }
            return ReadFruData.decodeFruData( fruData );
        }
        catch ( IPMIException e )
        {
            logger.error( "Exception while executing the task ServerInfo: " + e.getCompletionCode() + ":"
                + e.getMessage() );
            logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
            throw new IpmiServiceResponseException( e.getCompletionCode() );
        }
    }
}
