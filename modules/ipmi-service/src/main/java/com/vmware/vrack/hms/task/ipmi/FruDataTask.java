/* ********************************************************************************
 * FruDataTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ipmi;

import java.util.ArrayList;
import java.util.List;

/**
* Finds FRU Information obtained from FRU Inventory
* @author Yagnesh Chawda
*/
import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.api.async.ConnectionHandle;
import com.veraxsystems.vxipmi.api.sync.IpmiConnector;
import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.fru.BaseUnit;
import com.veraxsystems.vxipmi.coding.commands.fru.GetFruInventoryAreaInfo;
import com.veraxsystems.vxipmi.coding.commands.fru.GetFruInventoryAreaInfoResponseData;
import com.veraxsystems.vxipmi.coding.commands.fru.ReadFruData;
import com.veraxsystems.vxipmi.coding.commands.fru.ReadFruDataResponseData;
import com.veraxsystems.vxipmi.coding.commands.sdr.ReserveSdrRepository;
import com.veraxsystems.vxipmi.coding.commands.sdr.ReserveSdrRepositoryResponseData;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.resource.fru.ChassisInfo;
import com.vmware.vrack.hms.common.resource.fru.ChassisType;
import com.vmware.vrack.hms.common.resource.fru.DcLoadInfo;
import com.vmware.vrack.hms.common.resource.fru.DcOutputInfo;
import com.vmware.vrack.hms.common.resource.fru.EntityId;
import com.vmware.vrack.hms.common.resource.fru.ExtendedCompatibilityInfo;
import com.vmware.vrack.hms.common.resource.fru.FruRecord;
import com.vmware.vrack.hms.common.resource.fru.FruType;
import com.vmware.vrack.hms.common.resource.fru.ManagementAccessInfo;
import com.vmware.vrack.hms.common.resource.fru.ManagementAccessRecordType;
import com.vmware.vrack.hms.common.resource.fru.OemInfo;
import com.vmware.vrack.hms.common.resource.fru.PowerSupplyInfo;
import com.vmware.vrack.hms.common.resource.fru.ProductInfo;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;

/**
 * Field Replaceable Unit (FRU) related Ipmi task
 * 
 * @author Vmware
 */
public class FruDataTask
{
    private static Logger logger = Logger.getLogger( FruDataTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in FRU Data Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    IpmiTaskConnector connector = null;

    /**
     * Id of the built-in, default FRU
     */
    private static final int DEFAULT_FRU_ID = 0;

    /**
     * Size of data transmitted in single ReadFru command. Bigger values will improve performance. If server is
     * returning "Invalid data field in Request." error during ReadFru command, FRU_READ_PACKET_SIZE should be
     * decreased.
     */
    private int FRU_READ_PACKET_SIZE = 16;

    // private int nextRecId = 0;
    public FruDataTask( ServiceHmsNode node, IpmiTaskConnector connector )
    {
        this( node );
        this.connector = connector;
    }

    public FruDataTask( ServiceHmsNode node )
    {
        this.node = (ServiceServerNode) node;
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

    /**
     * @return
     * @throws Exception
     */
    public List<Object> executeTask()
        throws Exception, IpmiServiceResponseException
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request execute ipmi FruData task for Node " + node.getNodeID() );
            try
            {
                ReserveSdrRepositoryResponseData reservation =
                    (ReserveSdrRepositoryResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                             new ReserveSdrRepository( IpmiVersion.V20,
                                                                                                                       connector.getCipherSuite(),
                                                                                                                       AuthenticationType.RMCPPlus ) );
                logger.debug( "Trying to get FRU data via IPMI for node [ " + node.getNodeID() + " ]" );
                List<Object> fruInfo = processFru( connector.getConnector(), connector.getHandle(), DEFAULT_FRU_ID );
                logger.debug( "FRU data received and processed successfully for Node [ " + node.getNodeID()
                    + " ], FruInfo object [ " + String.valueOf( fruInfo ) + " ]" );
                return fruInfo;
            }
            catch ( IPMIException e )
            {
                logger.error( "Exception while executing the task FruData: " + e.getCompletionCode() + ":"
                    + e.getMessage() );
                logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
                throw new IpmiServiceResponseException( e.getCompletionCode() );
            }
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
     * @param connector
     * @param handle
     * @param fruId
     * @return
     * @throws Exception
     */
    private List<Object> processFru( IpmiConnector connector, ConnectionHandle handle, int fruId )
        throws Exception, IpmiServiceResponseException
    {
        List<ReadFruDataResponseData> fruData = new ArrayList<ReadFruDataResponseData>();
        List<Object> fruRecords = new ArrayList<Object>();
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
                }
            }
            try
            {
                // after collecting all the data, we can combine and parse it
                List records = ReadFruData.decodeFruData( fruData );
                int fruRecordsCount = records.size();
                for ( int i = 0; i < fruRecordsCount; i++ )
                {
                    com.veraxsystems.vxipmi.coding.commands.fru.record.FruRecord record =
                        (com.veraxsystems.vxipmi.coding.commands.fru.record.FruRecord) records.get( i );
                    // now we can for example display received info about board
                    // System.out.println("FRU Record Data :"+ record.toString());
                    FruRecord fruInstance = null;
                    if ( record instanceof com.veraxsystems.vxipmi.coding.commands.fru.record.BoardInfo )
                    {
                        com.veraxsystems.vxipmi.coding.commands.fru.record.BoardInfo bi =
                            (com.veraxsystems.vxipmi.coding.commands.fru.record.BoardInfo) record;
                        fruInstance = new BoardInfo();
                        BoardInfo boardInfo = (BoardInfo) fruInstance;
                        boardInfo.setFruType( FruType.Board );
                        boardInfo.setBoardSerialNumber( bi.getBoardSerialNumber() );
                        boardInfo.setBoardManufacturer( bi.getBoardManufacturer() );
                        boardInfo.setBoardProductName( bi.getBoardProductName() );
                    }
                    else if ( record instanceof com.veraxsystems.vxipmi.coding.commands.fru.record.ProductInfo )
                    {
                        com.veraxsystems.vxipmi.coding.commands.fru.record.ProductInfo pi =
                            (com.veraxsystems.vxipmi.coding.commands.fru.record.ProductInfo) record;
                        fruInstance = new ProductInfo();
                        ProductInfo prodcutInfo = (ProductInfo) fruInstance;
                        prodcutInfo.setFruType( FruType.Product );
                        prodcutInfo.setManufacturerName( pi.getManufacturerName() );
                        prodcutInfo.setProductName( pi.getProductName() );
                        prodcutInfo.setProductModelNumber( pi.getProductModelNumber() );
                        prodcutInfo.setProductSerialNumber( pi.getProductSerialNumber() );
                        prodcutInfo.setProductVersion( pi.getProductVersion() );
                        prodcutInfo.setAssetTag( pi.getAssetTag() );
                        prodcutInfo.setCustomProductInfo( pi.getCustomProductInfo() );
                    }
                    else if ( record instanceof com.veraxsystems.vxipmi.coding.commands.fru.record.ChassisInfo )
                    {
                        com.veraxsystems.vxipmi.coding.commands.fru.record.ChassisInfo ci =
                            (com.veraxsystems.vxipmi.coding.commands.fru.record.ChassisInfo) record;
                        fruInstance = new ChassisInfo();
                        ChassisInfo chassisInfo = (ChassisInfo) fruInstance;
                        chassisInfo.setFruType( FruType.Chassis );
                        chassisInfo.setChassisType( ChassisType.parseInt( ci.getChassisType().getCode() ) );
                        chassisInfo.setChassisSerialNumber( ci.getChassisSerialNumber() );
                        chassisInfo.setChassisPartNumber( ci.getChassisPartNumber() );
                    }
                    else if ( record instanceof com.veraxsystems.vxipmi.coding.commands.fru.record.PowerSupplyInfo )
                    {
                        com.veraxsystems.vxipmi.coding.commands.fru.record.PowerSupplyInfo psi =
                            (com.veraxsystems.vxipmi.coding.commands.fru.record.PowerSupplyInfo) record;
                        fruInstance = new PowerSupplyInfo();
                        PowerSupplyInfo powerSupplyInfo = (PowerSupplyInfo) fruInstance;
                        powerSupplyInfo.setFruType( FruType.Power_Supply );
                        powerSupplyInfo.setCapacity( psi.getCapacity() );
                        powerSupplyInfo.setPeakVa( psi.getPeakVa() );
                        powerSupplyInfo.setMaximumInrush( psi.getMaximumInrush() );
                        powerSupplyInfo.setLowEndInputVoltage1( psi.getLowEndInputVoltage1() );
                        powerSupplyInfo.setLowEndInputVoltage2( psi.getLowEndInputVoltage2() );
                        powerSupplyInfo.setLowEndInputFrequencyRange( psi.getLowEndInputFrequencyRange() );
                        powerSupplyInfo.setHighEndInputFrequencyRange( psi.getHighEndInputFrequencyRange() );
                        powerSupplyInfo.setHighEndInputVoltage1( psi.getHighEndInputVoltage1() );
                        powerSupplyInfo.setHighEndInputVoltage2( psi.getHighEndInputVoltage2() );
                    }
                    else if ( record instanceof com.veraxsystems.vxipmi.coding.commands.fru.record.DcOutputInfo )
                    {
                        com.veraxsystems.vxipmi.coding.commands.fru.record.DcOutputInfo ci =
                            (com.veraxsystems.vxipmi.coding.commands.fru.record.DcOutputInfo) record;
                        fruInstance = new DcOutputInfo();
                        DcOutputInfo dcOutputInfo = (DcOutputInfo) fruInstance;
                        dcOutputInfo.setFruType( FruType.DC_Output );
                        dcOutputInfo.setOutputNumber( ci.getOutputNumber() );
                        dcOutputInfo.setNominalVoltage( ci.getNominalVoltage() );
                        dcOutputInfo.setMaximumNegativeDeviation( ci.getMaximumNegativeDeviation() );
                        dcOutputInfo.setMaximumPositiveDeviation( ci.getMaximumPositiveDeviation() );
                    }
                    else if ( record instanceof com.veraxsystems.vxipmi.coding.commands.fru.record.DcLoadInfo )
                    {
                        com.veraxsystems.vxipmi.coding.commands.fru.record.DcLoadInfo ci =
                            (com.veraxsystems.vxipmi.coding.commands.fru.record.DcLoadInfo) record;
                        fruInstance = new DcLoadInfo();
                        DcLoadInfo dcLoadInfo = (DcLoadInfo) fruInstance;
                        dcLoadInfo.setFruType( FruType.DC_Load );
                        dcLoadInfo.setOutputNumber( ci.getOutputNumber() );
                        dcLoadInfo.setNominalVoltage( ci.getNominalVoltage() );
                        dcLoadInfo.setMinimumVoltage( ci.getMinimumVoltage() );
                        dcLoadInfo.setMaximumVoltage( ci.getMaximumVoltage() );
                        dcLoadInfo.setMinimumCurrentLoad( ci.getMinimumCurrentLoad() );
                        dcLoadInfo.setMaximumCurrentLoad( ci.getMaximumCurrentLoad() );
                    }
                    else if ( record instanceof com.veraxsystems.vxipmi.coding.commands.fru.record.ManagementAccessInfo )
                    {
                        com.veraxsystems.vxipmi.coding.commands.fru.record.ManagementAccessInfo ci =
                            (com.veraxsystems.vxipmi.coding.commands.fru.record.ManagementAccessInfo) record;
                        fruInstance = new ManagementAccessInfo();
                        ManagementAccessInfo managementAccessInfo = (ManagementAccessInfo) fruInstance;
                        managementAccessInfo.setFruType( FruType.Management_Access );
                        managementAccessInfo.setRecordType( ManagementAccessRecordType.parseInt( ci.getRecordType().getCode() ) );
                        managementAccessInfo.setAccessInfo( ci.getAccessInfo() );
                    }
                    else if ( record instanceof com.veraxsystems.vxipmi.coding.commands.fru.record.ExtendedCompatibilityInfo )
                    {
                        com.veraxsystems.vxipmi.coding.commands.fru.record.ExtendedCompatibilityInfo ci =
                            (com.veraxsystems.vxipmi.coding.commands.fru.record.ExtendedCompatibilityInfo) record;
                        fruInstance = new ExtendedCompatibilityInfo();
                        ExtendedCompatibilityInfo extendedCompatibilityInfo = (ExtendedCompatibilityInfo) fruInstance;
                        extendedCompatibilityInfo.setFruType( FruType.Extended_Compatibility );
                        extendedCompatibilityInfo.setManufacturerId( ci.getManufacturerId() );
                        extendedCompatibilityInfo.setEntityId( EntityId.parseInt( ci.getEntityId().getCode() ) );
                        extendedCompatibilityInfo.setCompatibilityBase( ci.getCompatibilityBase() );
                        extendedCompatibilityInfo.setCodeStart( ci.getCodeStart() );
                        extendedCompatibilityInfo.setCodeRangeMasks( ci.getCodeRangeMasks() );
                    }
                    else if ( record instanceof com.veraxsystems.vxipmi.coding.commands.fru.record.OemInfo )
                    {
                        com.veraxsystems.vxipmi.coding.commands.fru.record.OemInfo ci =
                            (com.veraxsystems.vxipmi.coding.commands.fru.record.OemInfo) record;
                        fruInstance = new OemInfo();
                        OemInfo oemInfo = (OemInfo) fruInstance;
                        oemInfo.setFruType( FruType.Oem );
                        oemInfo.setManufacturerId( ci.getManufacturerId() );
                        oemInfo.setOemData( ci.getOemData() );
                    }
                    else
                    {
                        logger.error( "Other format: " + record.getClass().getSimpleName() );
                    }
                    if ( fruInstance != null )
                        fruRecords.add( fruInstance );
                }
            }
            catch ( Exception e )
            {
                logger.error( String.format( "Error while decoding / parsing FRU record for node [ %s ], ",
                                             node.getNodeID() ),
                              e );
            }
            return fruRecords;
        }
        catch ( IPMIException e )
        {
            logger.error( "Exception while getting the FRU Inventory Area info: " + e.getCompletionCode() + ":"
                + e.getMessage() );
            logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
            throw new IpmiServiceResponseException( e.getCompletionCode() );
        }
    }
}
