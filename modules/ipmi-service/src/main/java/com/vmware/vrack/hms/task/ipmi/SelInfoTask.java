/* ********************************************************************************
 * SelInfoTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ipmi;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.api.async.ConnectionHandle;
import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.sel.GetSelEntry;
import com.veraxsystems.vxipmi.coding.commands.sel.GetSelEntryResponseData;
import com.veraxsystems.vxipmi.coding.commands.sel.GetSelInfo;
import com.veraxsystems.vxipmi.coding.commands.sel.GetSelInfoResponseData;
import com.veraxsystems.vxipmi.coding.commands.sel.ReserveSel;
import com.veraxsystems.vxipmi.coding.commands.sel.ReserveSelResponseData;
import com.veraxsystems.vxipmi.coding.payload.CompletionCode;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.veraxsystems.vxipmi.connection.ConnectionException;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.fru.SensorType;
import com.vmware.vrack.hms.common.resource.sel.EventDirection;
import com.vmware.vrack.hms.common.resource.sel.ReadingType;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.resource.sel.SelRecord;
import com.vmware.vrack.hms.common.resource.sel.SelRecordType;
import com.vmware.vrack.hms.common.resource.sel.SelTask;
import com.vmware.vrack.hms.ipmi.util.SelUtil.SelUtil;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;

/**
 * SEL Info Task to get System Event Log (SEL) related information via BMC
 * 
 * @author Vmware
 */
public class SelInfoTask
{
    private static Logger logger = Logger.getLogger( SelInfoTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in SEL Info Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    IpmiTaskConnector connector;

    private SelTask task;

    private int maxSelCount;

    private SelFetchDirection direction;

    int nextRecId;

    private int END_REPO_RECORD_ID = 65535;

    private static final int MAX_CONNECT_TIMEOUT_RETRIES = 5;

    public SelInfoTask( ServiceHmsNode node, SelTask task, int maxSelCount, SelFetchDirection direction )
    {
        this.node = (ServiceServerNode) node;
        this.task = task;
        this.maxSelCount = maxSelCount;
        this.direction = direction;
    }

    public SelInfoTask( ServiceHmsNode node, IpmiTaskConnector connector, SelTask task, int maxSelCount,
                        SelFetchDirection direction )
    {
        this( node, task, maxSelCount, direction );
        this.connector = connector;
    }

    public SelInfo executeTask()
        throws Exception
    {
        SelInfo selInfo = null;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request to execute ipmi SelInfo task for Node " + node.getNodeID() );
            switch ( task )
            {
                case SelInfo:
                    selInfo = getSelInfo();
                    break;
                case SelDetails:
                    selInfo = getSelDetails( null );
                    break;
            }
            return selInfo;
        }
        else
        {
            String err = String.format( NULL_CONNECTOR_EXCEPTION_MSG, node,
                                        ( node instanceof ServiceServerNode ) ? "" : "NOT ", connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    public SelInfo executeTaskWithFilter( List<SelRecord> selFilters )
        throws Exception
    {
        SelInfo selInfo = null;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request to execute ipmi SelInfo task for Node " + node.getNodeID() );
            switch ( task )
            {
                case SelInfo:
                    selInfo = getSelInfo();
                    break;
                case SelDetails:
                    selInfo = getSelDetails( selFilters );
                    break;
            }
            return selInfo;
        }
        else
        {
            String err = String.format( NULL_CONNECTOR_EXCEPTION_MSG, node,
                                        ( node instanceof ServiceServerNode ) ? "" : "NOT ", connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    public SelInfo getSelInfo()
        throws Exception, IpmiServiceResponseException
    {
        SelInfo info = new SelInfo();
        try
        {
            GetSelInfoResponseData selInfoResponseData =
                (GetSelInfoResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                               new GetSelInfo( IpmiVersion.V20,
                                                                                               connector.getCipherSuite(),
                                                                                               AuthenticationType.RMCPPlus ) );
            info.setSelVersion( selInfoResponseData.getSelVersion() );
            info.setLastAddtionTimeStamp( selInfoResponseData.getAdditionTimestamp() );
            info.setLastEraseTimeStamp( selInfoResponseData.getEraseTimestamp() );
            info.setTotalSelCount( selInfoResponseData.getEntriesCount() );
            info.setFetchedSelCount( 0 );
        }
        catch ( IPMIException e )
        {
            logger.error( "Exception while executing the task SelInfo: " + e.getCompletionCode() + ":"
                + e.getMessage() );
            logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
            throw new IpmiServiceResponseException( e.getCompletionCode() );
        }
        return info;
    }

    public SelInfo getSelDetails( List<SelRecord> selFilters )
        throws Exception, IpmiServiceResponseException
    {
        SelInfo info = new SelInfo();
        try
        {
            GetSelInfoResponseData selInfoResponseData =
                (GetSelInfoResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                               new GetSelInfo( IpmiVersion.V20,
                                                                                               connector.getCipherSuite(),
                                                                                               AuthenticationType.RMCPPlus ) );
            info.setSelVersion( selInfoResponseData.getSelVersion() );
            info.setLastAddtionTimeStamp( selInfoResponseData.getAdditionTimestamp() );
            info.setLastEraseTimeStamp( selInfoResponseData.getEraseTimestamp() );
            info.setTotalSelCount( selInfoResponseData.getEntriesCount() );
            // Check if the requested number of entries to fetch is valid or not. i.e Check if this much entries are
            // present in server.
            if ( maxSelCount > info.getTotalSelCount() )
            {
                // Set it to the maximum number of entries available
                maxSelCount = info.getTotalSelCount();
            }
            List<SelRecord> records = getAllSelRecord( selFilters );
            info.setSelRecords( records );
            info.setFetchedSelCount( ( records != null ) ? records.size() : 0 );
            return info;
        }
        catch ( IPMIException e )
        {
            logger.error( "Exception while executing the task SelInfo: " + e.getCompletionCode() + ":"
                + e.getMessage() );
            logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
            throw new IpmiServiceResponseException( e.getCompletionCode() );
        }
    }

    public List<SelRecord> getAllSelRecord( List<SelRecord> selFilters )
        throws Exception
    {
        List<SelRecord> selRecords = new ArrayList<SelRecord>();
        ConnectionHandle connectionHandle = connector.getHandle();
        // Change timeout of this particular connection (default value for
        // further connections does not change)
        connector.getConnector().setTimeout( connectionHandle, 2750 );
        ReserveSelResponseData reserveSelResponseData =
            (ReserveSelResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                           new ReserveSel( IpmiVersion.V20,
                                                                                           connector.getCipherSuite(),
                                                                                           AuthenticationType.RMCPPlus ) );
        int reservationId = reserveSelResponseData.getReservationId();
        if ( direction == SelFetchDirection.RecentEntries )
        {
            nextRecId = 65535;
            END_REPO_RECORD_ID = 0;
            int entriesCount = 0;
            int connectionTimeOutRetries = 0;
            while ( nextRecId > END_REPO_RECORD_ID && entriesCount < maxSelCount )
            {
                SelRecord record = null;
                try
                {
                    record = getSelRecord( reservationId );
                    if ( record != null )
                    {
                        if ( selFilters != null && !selFilters.isEmpty() )
                        {
                            for ( SelRecord filter : selFilters )
                            {
                                // Compare Sel Record,
                                // if it matches with the filtering conditions, it will return itself, otherwise null
                                SelRecord selRecord = SelUtil.compareSelRecord( record, filter );
                                if ( selRecord != null )
                                {
                                    selRecords.add( record );
                                    entriesCount++;
                                }
                            }
                        }
                        else
                        {
                            selRecords.add( record );
                            entriesCount++;
                        }
                        // selRecords.add(record);
                        // entriesCount++;
                    }
                }
                catch ( IPMIException e )
                {
                    if ( e.getCompletionCode() == CompletionCode.DataNotPresent )
                    {
                        // logger.debug(e.getMessage());
                        if ( direction == SelFetchDirection.RecentEntries )
                        {
                            nextRecId -= 1;
                        }
                    }
                    else
                    {
                        logger.error( "Exception while executing task SelInfo at getAllSelRecord: "
                            + e.getCompletionCode() + ":" + e.getMessage() );
                        logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
                        throw new IpmiServiceResponseException( e.getCompletionCode() );
                    }
                }
                catch ( Exception e )
                {
                    if ( e instanceof ConnectionException )
                    {
                        if ( connectionTimeOutRetries >= MAX_CONNECT_TIMEOUT_RETRIES )
                        {
                            throw e;
                        }
                        if ( e.getMessage().contains( "timed out" ) )
                        {
                            connectionTimeOutRetries++;
                        }
                    }
                }
            }
        }
        else if ( direction == SelFetchDirection.OldestEntries )
        {
            nextRecId = 0;
            int entriesCount = 0;
            while ( nextRecId < END_REPO_RECORD_ID && entriesCount < maxSelCount )
            {
                SelRecord record = null;
                try
                {
                    record = getSelRecord( reservationId );
                    if ( record != null )
                    {
                        if ( selFilters != null && !selFilters.isEmpty() )
                        {
                            for ( SelRecord filter : selFilters )
                            {
                                // Compare Sel Record,
                                // if it matches with the filtering conditions, it will return itself, otherwise null
                                SelRecord selRecord = SelUtil.compareSelRecord( record, filter );
                                if ( selRecord != null )
                                {
                                    selRecords.add( record );
                                    entriesCount++;
                                }
                            }
                        }
                        else
                        {
                            selRecords.add( record );
                            entriesCount++;
                        }
                    }
                }
                catch ( IPMIException e )
                {
                    if ( e.getCompletionCode() == CompletionCode.DataNotPresent )
                    {
                        // logger.debug(e.getMessage());
                    }
                    else
                    {
                        logger.error( "Exception while executing task SelInfo at getAllSelRecord: "
                            + e.getCompletionCode() + ":" + e.getMessage() );
                        logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
                        throw new IpmiServiceResponseException( e.getCompletionCode() );
                    }
                }
            }
        }
        return selRecords;
    }

    private SelRecord getSelRecord( int reservationId )
        throws Exception
    {
        SelRecord record = null;
        ConnectionHandle connectionHandle = connector.getHandle();
        try
        {
            GetSelEntryResponseData data =
                (GetSelEntryResponseData) connector.getConnector().sendMessage( connectionHandle,
                                                                                new GetSelEntry( IpmiVersion.V20,
                                                                                                 connectionHandle.getCipherSuite(),
                                                                                                 AuthenticationType.RMCPPlus,
                                                                                                 reservationId,
                                                                                                 nextRecId ) );
            com.veraxsystems.vxipmi.coding.commands.sel.SelRecord ipmiRecord = data.getSelRecord();
            // TODO: Convert Sel Record Object from Verax to one from Common
            if ( ipmiRecord != null )
            {
                record = new SelRecord();
                record.setEvent( ReadingType.parseInt( ipmiRecord.getEvent().getCode() ) );
                record.setEventDirection( EventDirection.parseInt( ipmiRecord.getEventDirection().getCode() ) );
                record.setReading( ipmiRecord.getReading() );
                record.setRecordId( ipmiRecord.getRecordId() );
                record.setRecordType( SelRecordType.parseInt( ipmiRecord.getRecordType().getCode() ) );
                record.setSensorNumber( ipmiRecord.getSensorNumber() );
                record.setSensorType( SensorType.parseInt( ipmiRecord.getSensorType().getCode() ) );
                record.setTimestamp( ipmiRecord.getTimestamp() );
            }
            // ... and update the ID of the next record
            if ( direction == SelFetchDirection.RecentEntries )
            {
                nextRecId = record.getRecordId() - 1;
            }
            else if ( direction == SelFetchDirection.OldestEntries )
            {
                nextRecId = data.getNextRecordId();
            }
        }
        catch ( IPMIException e )
        {
            if ( e.getCompletionCode() == CompletionCode.CannotRespond
                || e.getCompletionCode() == CompletionCode.UnspecifiedError )
            {
                // TODO: Set Next Record Id 1 greater than current.
                if ( direction == SelFetchDirection.RecentEntries )
                {
                    nextRecId -= 1;
                }
                else if ( direction == SelFetchDirection.OldestEntries )
                {
                    nextRecId = nextRecId += 1;
                }
            }
            else
            {
                logger.error( "Exception while executing task SelInfo at getSelRecord: " + e.getCompletionCode() + ":"
                    + e.getMessage() );
                logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
                throw new IpmiServiceResponseException( e.getCompletionCode() );
            }
        }
        catch ( Exception e )
        {
            throw e;
        }
        return record;
    }
}
