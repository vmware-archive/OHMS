/* ********************************************************************************
 * IpmiServiceExecutor.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.ipmiservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.coding.commands.session.SessionCustomPayload;
import com.veraxsystems.vxipmi.coding.security.CipherSuite;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.EntityId;
import com.vmware.vrack.hms.common.resource.fru.SensorType;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.resource.sel.SelRecord;
import com.vmware.vrack.hms.common.resource.sel.SelTask;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.task.ipmi.AcpiPowerStateTask;
import com.vmware.vrack.hms.task.ipmi.ChassisIdentifyTask;
import com.vmware.vrack.hms.task.ipmi.ColdResetBmcTask;
import com.vmware.vrack.hms.task.ipmi.FindMacAddressTask;
import com.vmware.vrack.hms.task.ipmi.FruDataTask;
import com.vmware.vrack.hms.task.ipmi.GetSystemBootOptionsTask;
import com.vmware.vrack.hms.task.ipmi.IpmiTaskConnector;
import com.vmware.vrack.hms.task.ipmi.IsHostAvailableTask;
import com.vmware.vrack.hms.task.ipmi.ListBmcUsersTask;
import com.vmware.vrack.hms.task.ipmi.PowerCycleServerTask;
import com.vmware.vrack.hms.task.ipmi.PowerDownServerTask;
import com.vmware.vrack.hms.task.ipmi.PowerResetServerTask;
import com.vmware.vrack.hms.task.ipmi.PowerStatusServerTask;
import com.vmware.vrack.hms.task.ipmi.PowerUpServerTask;
import com.vmware.vrack.hms.task.ipmi.SelInfoTask;
import com.vmware.vrack.hms.task.ipmi.SelfTestTask;
import com.vmware.vrack.hms.task.ipmi.SensorStatusTask;
import com.vmware.vrack.hms.task.ipmi.ServerInfoTask;
import com.vmware.vrack.hms.task.ipmi.SetSystemBootOptionsTask;
import com.vmware.vrack.hms.utils.Constants;

/**
 * Class to perform all IPMI related functions
 * 
 * @author VMware, Inc.
 */
public class IpmiServiceExecutor
    implements IpmiService
{
    private static Logger logger = Logger.getLogger( IpmiServiceExecutor.class );

    private final String NULL_NODE_EXCEPTION_MSG = "Node is [ %s ] or not ServiceServerNode.";

    private final String NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG =
        "Node is [ %s ] or not ServiceServerNode and connector is [ %s ].";

    private int cipherSuiteIndex = 3;

    private boolean encryptData = true;

    private CipherSuite cipherSuite;

    private SessionCustomPayload customSessionPayload;

    private Map<String, IpmiConnectionSettings> connectionSettings = new HashMap<String, IpmiConnectionSettings>();

    public IpmiServiceExecutor()
    {
        super();
    }

    public IpmiServiceExecutor( int cipherSuiteIndex )
    {
        this.cipherSuiteIndex = cipherSuiteIndex;
    }

    public IpmiServiceExecutor( CipherSuite cipherSuite, boolean encryptData,
                                SessionCustomPayload customSessionPayload )
    {
        this.cipherSuite = cipherSuite;
        this.encryptData = encryptData;
        this.customSessionPayload = customSessionPayload;
    }

    /**
     * Method to perform cold reset through ipmi
     * 
     * @param node
     * @return boolean
     */
    @Override
    public boolean coldResetServer( ServiceHmsNode node )
        throws Exception
    {
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to execute cold reset of server for Node " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                boolean status = coldResetServer( node, connector );
                logger.info( String.format( "Cold Reset Status is [ %s ]", ( status ? "ON" : "OFF" ) ) );
                return status;
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connnector." );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to perform cold reset through ipmi
     * 
     * @param node
     * @param connector
     * @return boolean
     */
    @Override
    public boolean coldResetServer( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            ColdResetBmcTask coldResetTask = new ColdResetBmcTask( node, connector );
            boolean status = coldResetTask.executeTask();
            logger.info( "Cold Reset status obtained for node: " + node.getNodeID() );
            return status;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Borrow an IPMI connector for given server node from IpmiConnectionPool.
     *
     * @param ServiceServerNode
     * @return IpmiTaskConnector
     * @throws Exception
     */
    private IpmiTaskConnector getIpmiTaskConnector( ServiceServerNode node )
        throws Exception
    {
        IpmiConnectionSettings settings = null;
        synchronized ( this )
        {
            if ( connectionSettings.containsKey( node.getNodeID() ) )
                settings = connectionSettings.get( node.getNodeID() );
            else
            {
                settings =
                    createConnectionSettings( node, cipherSuiteIndex, encryptData, cipherSuite, customSessionPayload );
                connectionSettings.put( node.getNodeID(), settings );
            }
        }
        return IpmiConnectionPool.getInstance().getPool().borrowObject( settings );
    }

    /**
     * Return IPMI connector back to the IpmiConnectionPool.
     *
     * @param ServiceServerNode
     * @param IpmiTaskConnector
     * @throws Exception
     */
    private void returnIpmiTaskConnector( ServiceServerNode node, IpmiTaskConnector connector )
        throws Exception
    {
        IpmiConnectionSettings settings = connectionSettings.get( node.getNodeID() );
        IpmiConnectionPool.getInstance().getPool().returnObject( settings, connector );
    }

    /**
     * Return IpmiConnectionSettings for a given server node, to be used as a key in pool.
     */
    private IpmiConnectionSettings createConnectionSettings( ServiceServerNode node, int cipherSuiteIndex,
                                                             boolean encryptData, CipherSuite cipherSuite,
                                                             SessionCustomPayload customSessionPayload )
    {
        IpmiConnectionSettings settings = new IpmiConnectionSettings();
        settings.setNode( node );
        settings.setCipherSuite( cipherSuite );
        settings.setCipherSuiteIndex( cipherSuiteIndex );
        settings.setEncryptData( encryptData );
        settings.setSessionOpenPayload( customSessionPayload );
        return settings;
    }

    /**
     * Method to perform power cycle server through ipmi
     * 
     * @param node
     * @return boolean
     */
    @Override
    public boolean powerCycleServer( ServiceHmsNode node )
        throws Exception
    {
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to execute power cycle on server for Node " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                boolean status = powerCycleServer( node, connector );
                logger.info( String.format( "Power Cycle status is [ %s ]", ( status ? "ON" : "OFF" ) ) );
                return status;
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to perform power cycle server through ipmi
     * 
     * @param node
     * @param connector
     * @return boolean
     */
    @Override
    public boolean powerCycleServer( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            PowerCycleServerTask powerCycleServerTask = new PowerCycleServerTask( node, connector );
            boolean status = powerCycleServerTask.executeTask();
            logger.info( "Power Cycle status obtained for node: " + node.getNodeID() );
            return status;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to perform power down server through ipmi
     * 
     * @param node
     * @return boolean
     */
    @Override
    public boolean powerDownServer( ServiceHmsNode node )
        throws Exception
    {
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to execute power down on server for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                boolean status = powerDownServer( node, connector );
                logger.info( String.format( "Power Down status is [ %s ]", ( status ? "ON" : "OFF" ) ) );
                return status;
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to perform power down server through ipmi
     * 
     * @param node
     * @param connector
     * @return boolean
     */
    @Override
    public boolean powerDownServer( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            PowerDownServerTask powerDownServerTask = new PowerDownServerTask( node, connector );
            boolean status = powerDownServerTask.executeTask();
            logger.info( "Power Cycle status obtained for node: " + node.getNodeID() );
            return status;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to perform power reset server through ipmi
     * 
     * @param node
     * @return boolean
     */
    @Override
    public boolean powerResetServer( ServiceHmsNode node )
        throws Exception
    {
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to execute power reset on server for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                boolean status = powerResetServer( node, connector );
                logger.info( String.format( "Power Reset Status is [ %s ]", ( status ? "ON" : "OFF" ) ) );
                return status;
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to perform power reset server through ipmi
     * 
     * @param node
     * @param connector
     * @return boolean
     */
    @Override
    public boolean powerResetServer( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            PowerResetServerTask powerResetServerTask = new PowerResetServerTask( node, connector );
            boolean status = powerResetServerTask.executeTask();
            logger.info( "Power Reset status obtained for node: " + node.getNodeID() );
            return status;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to perform power on server through ipmi
     * 
     * @param node
     * @return boolean
     */
    @Override
    public boolean powerUpServer( ServiceHmsNode node )
        throws Exception
    {
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to execute power up on server for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                boolean status = powerUpServer( node, connector );
                logger.info( String.format( "Power Up Status is [ %s ]", ( status ? "ON" : "OFF" ) ) );
                return status;
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to perform power on server through ipmi
     * 
     * @param node
     * @param connector
     * @return boolean
     */
    @Override
    public boolean powerUpServer( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            PowerUpServerTask powerUpServerTask = new PowerUpServerTask( node, connector );
            boolean status = powerUpServerTask.executeTask();
            logger.info( "Power Up status obtained for node: " + node.getNodeID() );
            return status;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to do BMC Self Test through ipmi
     * 
     * @param node
     * @return SelfTestResults
     */
    @Override
    public SelfTestResults selfTest( ServiceHmsNode node )
        throws Exception
    {
        SelfTestResults results = null;
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to execute self test through ipmi on server for Node: "
                + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                results = selfTest( node, connector );
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
            logger.info( "Self Test results retrieved" );
            return results;
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to do BMC Self Test through ipmi
     * 
     * @param node
     * @param connector
     * @return SelfTestResults
     */
    @Override
    public SelfTestResults selfTest( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        SelfTestResults results = null;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            SelfTestTask selfTestTask = new SelfTestTask( node, connector );
            results = selfTestTask.executeTask();
            logger.info( "Self Test through Ipmi obtained for node: " + node.getNodeID() );
            return results;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get ACPI power state through ipmi
     * 
     * @param node
     * @return AcpiPowerState
     */
    @Override
    public AcpiPowerState getAcpiPowerState( ServiceHmsNode node )
        throws Exception
    {
        AcpiPowerState acpiPowerState = null;
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to get ACPI power state on sever for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                acpiPowerState = getAcpiPowerState( node, connector );
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
            logger.info( "Acpi state retrieved" );
            return acpiPowerState;
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get ACPI power state through ipmi
     * 
     * @param node
     * @param connector
     * @return AcpiPowerState
     */
    @Override
    public AcpiPowerState getAcpiPowerState( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        AcpiPowerState powerState = null;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            AcpiPowerStateTask acpiPowerStateTask = new AcpiPowerStateTask( node, connector );
            powerState = acpiPowerStateTask.executeTask();
            logger.info( "Acpi Power State through Ipmi obtained for node: " + node.getNodeID() );
            return powerState;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get List of BMC users through ipmi
     * 
     * @param node
     * @return List<BmcUser>
     */
    @Override
    public List<BmcUser> getBmcUsers( ServiceHmsNode node )
        throws Exception
    {
        List<BmcUser> bmcUsers = new ArrayList<BmcUser>();
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to get BMC Users for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                bmcUsers = getBmcUsers( node, connector );
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
            logger.info( "BMC Users retrieved" );
            return bmcUsers;
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get List of BMC users through ipmi
     * 
     * @param node
     * @param connector
     * @return List<BmcUser>
     */
    @Override
    public List<BmcUser> getBmcUsers( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        List<BmcUser> userList = new ArrayList<BmcUser>();
        if ( node instanceof ServiceServerNode && connector != null )
        {
            ListBmcUsersTask bmcUsersTask = new ListBmcUsersTask( node, connector );
            userList = bmcUsersTask.executeTask();
            logger.info( "BMC Users obtained for node: " + node.getNodeID() );
            return userList;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get Boot options through ipmi
     * 
     * @param node
     * @return SystemBootOptions
     */
    @Override
    public SystemBootOptions getBootOptions( ServiceHmsNode node )
        throws Exception
    {
        SystemBootOptions systemBootOptions = null;
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to get Boot Options for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                systemBootOptions = getBootOptions( node, connector );
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
            logger.info( "Boot Options retrieved" );
            return systemBootOptions;
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get Boot options through ipmi
     * 
     * @param node
     * @param connector
     * @return SystemBootOptions
     */
    @Override
    public SystemBootOptions getBootOptions( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        SystemBootOptions systemBootOptions = null;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            GetSystemBootOptionsTask bootOptionsTask = new GetSystemBootOptionsTask( node, connector );
            systemBootOptions = bootOptionsTask.executeTask();
            logger.info( "Boot Options obtained for node: " + node.getNodeID() );
            return systemBootOptions;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get Field Replaceable Unit (FRU) information through ipmi
     * 
     * @param node
     * @return List<Object>
     */
    @Override
    public List<Object> getFruInfo( ServiceHmsNode node )
        throws Exception
    {
        return getFruInfo( node, null );
    }

    /**
     * Method to get Field Replaceable Unit (FRU) information through ipmi
     * 
     * @param node
     * @param fruReadPacketSize
     * @return List<Object>
     */
    @Override
    public List<Object> getFruInfo( ServiceHmsNode node, Integer fruReadPacketSize )
        throws Exception
    {
        List<Object> fruInfo = new ArrayList<>();
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Get FRU information for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                fruInfo = getFruInfo( node, connector, fruReadPacketSize );
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
            logger.info( "FRU Details/Info retrieved" );
            return fruInfo;
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get Field Replaceable Unit (FRU) information through ipmi
     * 
     * @param node
     * @param connector
     * @param fruReadPacketSize
     * @return List<Object>
     */
    @Override
    public List<Object> getFruInfo( ServiceHmsNode node, IpmiTaskConnector connector, Integer fruReadPacketSize )
        throws Exception
    {
        List<Object> fruInfo = new ArrayList<>();
        if ( node instanceof ServiceServerNode && connector != null )
        {
            FruDataTask fruDataTask = new FruDataTask( node, connector );
            if ( fruReadPacketSize != null )
            {
                fruDataTask.setFruReadPacketSize( fruReadPacketSize );
            }
            fruInfo = fruDataTask.executeTask();
            logger.info( "Field Replacable Unit info obtained from node: " + node.getNodeID() );
            return fruInfo;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get Management node MAC address through ipmi
     * 
     * @param node
     * @return String
     */
    @Override
    public String getMacAddress( ServiceHmsNode node )
        throws Exception
    {
        String macAddress = null;
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to get Mac Address for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                macAddress = getMacAddress( node, connector );
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
            logger.info( "Mac Address retrieved: " + macAddress );
            return macAddress;
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get Management node MAC address through ipmi
     * 
     * @param node
     * @param connector
     * @return String
     */
    @Override
    public String getMacAddress( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        String macAddress = null;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            FindMacAddressTask macAddressTask = new FindMacAddressTask( node, connector );
            macAddress = macAddressTask.executeTask();
            logger.info( "Mac Address obtained for node: " + node.getNodeID() );
            return macAddress;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to set Boot options through ipmi
     * 
     * @param node
     * @param bootOptions
     * @return boolean
     */
    @Override
    public boolean setBootOptions( ServiceHmsNode node, SystemBootOptions bootOptions )
        throws Exception
    {
        if ( node != null && node instanceof ServiceServerNode && bootOptions != null )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to set Boot Options for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                boolean status = setBootOptions( node, connector, bootOptions );
                logger.info( String.format( "Boot Options Status is [ %s ]", ( status ? "ON" : "OFF" ) ) );
                return status;
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
        }
        else
        {
            String err =
                String.format( "Node is [ %s ] or not ServiceServerNode and SystemBootOptions Object is [ %s ]. ", node,
                               bootOptions );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to set Boot options through ipmi
     * 
     * @param node
     * @param connector
     * @param bootOptions
     * @return boolean
     */
    @Override
    public boolean setBootOptions( ServiceHmsNode node, IpmiTaskConnector connector, SystemBootOptions bootOptions )
        throws Exception
    {
        if ( node != null && node instanceof ServiceServerNode && connector != null && bootOptions != null )
        {
            SetSystemBootOptionsTask setSystemBootOptionsTask =
                new SetSystemBootOptionsTask( node, connector, bootOptions );
            boolean status = setSystemBootOptionsTask.executeTask();
            logger.info( "Set Boot Options Status obtained for node: " + node.getNodeID() );
            return status;
        }
        else
        {
            String err =
                String.format( "Node is [ %s ] or not ServiceServerNode and Connector object is [ %s ] and SystemBootOptions Object is [ %s ]. ",
                               node, connector, bootOptions );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get Sensor data List through ipmi
     * 
     * @param node
     * @return List<Map<String, String>>
     */
    @Override
    public List<Map<String, String>> getSensorData( ServiceHmsNode node )
        throws Exception
    {
        return getSensorData( node, null, null, null, null );
    }

    /**
     * Method to get Sensor data List through ipmi
     * 
     * @param node
     * @param listsensorNumber
     * @return List<Map<String, String>>
     */
    @Override
    public List<Map<String, String>> getSensorData( ServiceHmsNode node, List<Integer> listsensorNumber )
        throws Exception
    {
        return getSensorData( node, null, null, null, listsensorNumber );
    }

    /**
     * Method to get Sensor data for a given Entity
     * 
     * @param node
     * @param listsensorNumber
     * @return List<Map<String, String>>
     */
    @Override
    public List<Map<String, String>> getSensorDataForSensorTypeAndEntity( ServiceHmsNode node,
                                                                          List<SensorType> typeList,
                                                                          List<EntityId> entityList )
                                                                              throws Exception
    {
        List<Map<String, String>> sensorData = null;
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to get Sensor Data for Sensor Type and Entity for Node: "
                + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                if ( node instanceof ServiceServerNode && connector != null )
                {
                    SensorStatusTask sensorTask = new SensorStatusTask( node, connector );
                    /*
                     * Set SDR record header size in {headerSize}, Size of the initial GetSdr message to get record
                     * header and size in {initalChunkSize}, Chunk size depending on buffer size of the IPMI server in
                     * {chunkSize}
                     */
                    sensorData = sensorTask.executeTask( typeList, entityList );
                    logger.info( "Sensor data for a given entity obtained for node: " + node.getNodeID() );
                    return sensorData;
                }
                else
                {
                    String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
                    logger.error( err );
                    throw new IllegalArgumentException( err );
                }
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get Sensor data List through ipmi
     * 
     * @param node
     * @param headerSize
     * @param initalChunkSize
     * @param chunkSize
     * @param listsensorNumber
     * @return List<Map<String, String>>
     */
    @Override
    public List<Map<String, String>> getSensorData( ServiceHmsNode node, Integer headerSize, Integer initalChunkSize,
                                                    Integer chunkSize, List<Integer> listsensorNumber )
                                                        throws Exception
    {
        List<Map<String, String>> sensorData = null;
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Get Sensor Data list for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                sensorData = getSensorData( node, connector, headerSize, initalChunkSize, chunkSize, listsensorNumber );
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
            logger.info( "Sensor Data list obtained." );
            return sensorData;
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /*
     * This method gets the sensor data record list, which is map of <string, string>
     * @param node
     * @param connector
     * @param headerSize
     * @param initalChunkSize
     * @param chunkSize
     * @param listSensorNumber - Board Sensor Number
     * @return List<Map<String, String>> The sensor return data contains Sensor name, Sensor number, Reading (for
     * threshold based), Unit, Sensor Type, Entity ID, state of the sensor and State Byte Code State - Can have more
     * than one sensor state for discrete sensor as sensor can have more than one state active. StateByteCode - Contains
     * state byte codes of the sensor. StateByteCode can have more than one state byte codes (for discrete sensor)
     * separated by space as sensor can have more than one state active. For example (discrete sensor): If the
     * StateByteCode is 880384 (0x0d6f00) - Drive Presence 0D - Sensor Type Code (Drive Slot/Bay) 6f - Sensor Specific
     * Reading type 00 - Sensor specific Offset (Drive Presence) If StateByteCode (for discrete sensor) has no value
     * meaning none of the state is active - All are Deasserted For example (threshold sensor) : If the StateByteCode is
     * 8 - "AboveUpperNonCritical" StateByteCode=0 (for threshold sensor) meaning state is "OK"
     */
    @Override
    public List<Map<String, String>> getSensorData( ServiceHmsNode node, IpmiTaskConnector connector,
                                                    Integer headerSize, Integer initialChunkSize, Integer chunkSize,
                                                    List<Integer> listsensorNumber )
                                                        throws Exception
    {
        List<Map<String, String>> sensorData = new ArrayList<>();
        if ( node instanceof ServiceServerNode && connector != null )
        {
            SensorStatusTask sensorTask = new SensorStatusTask( node, connector );
            /*
             * Set SDR record header size in {headerSize}, Size of the initial GetSdr message to get record header and
             * size in {initalChunkSize}, Chunk size depending on buffer size of the IPMI server in {chunkSize}
             */
            sensorTask.setChunkSizes( headerSize, initialChunkSize, chunkSize );
            sensorData = sensorTask.executeTask( listsensorNumber );
            logger.info( "Sensor Data record list obtained for node: " + node.getNodeID() );
            return sensorData;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get Server Node information (board product name, vendor etc)
     * 
     * @param node
     * @return ServerNodeInfo
     */
    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode node )
        throws Exception
    {
        return getServerInfo( node, null );
    }

    /**
     * Method to get Server Node information (board product name, vendor etc)
     * 
     * @param node
     * @param fruReadPacketSize
     * @return ServerNodeInfo
     */
    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode node, Integer fruReadPacketSize )
        throws Exception
    {
        return getServerInfo( node, fruReadPacketSize, null );
    }

    /**
     * Method to get Server Node information (board product name, vendor etc)
     * 
     * @param node
     * @param fruReadPacketSize
     * @param fruList
     * @return ServerNodeInfo
     */
    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode node, Integer fruReadPacketSize, ArrayList<Integer> fruList )
        throws Exception
    {
        ServerNodeInfo serverNodeInfo = null;
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to get Server Info for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                serverNodeInfo = getServerInfo( node, connector, fruReadPacketSize, fruList );
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
            logger.info( "Server Information retrieved." );
            return serverNodeInfo;
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get Server Node information (board product name, vendor etc)
     * 
     * @param node
     * @param connector
     * @param fruReadPacketSize
     * @return ServerNodeInfo
     */
    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode node, IpmiTaskConnector connector, Integer fruReadPacketSize )
        throws Exception
    {
        return getServerInfo( node, connector, fruReadPacketSize, null );
    }

    /**
     * Method to get Server Node information (board product name, vendor etc)
     * 
     * @param node
     * @param connector
     * @param fruReadPacketSize
     * @param fruList
     * @return ServerNodeInfo
     */
    @Override
    public ServerNodeInfo getServerInfo( ServiceHmsNode node, IpmiTaskConnector connector, Integer fruReadPacketSize,
                                         ArrayList<Integer> fruList )
                                             throws Exception
    {
        ServerNodeInfo serverNodeInfo = null;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            ServerInfoTask serverInfoTask = new ServerInfoTask( node, connector );
            if ( fruReadPacketSize != null )
            {
                serverInfoTask.setFruReadPacketSize( fruReadPacketSize );
            }
            if ( fruList != null )
            {
                serverInfoTask.setFruList( fruList );
            }
            serverNodeInfo = serverInfoTask.executeTask();
            logger.info( "Server Node Info obtained for node: " + node.getNodeID() );
            return serverNodeInfo;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get Server Power Status
     * 
     * @param node
     * @return boolean
     */
    @Override
    public boolean getServerPowerStatus( ServiceHmsNode node )
        throws Exception
    {
        boolean powerStatus;
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to get ServerPowerStatus for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                powerStatus = getServerPowerStatus( node, connector );
                logger.info( String.format( "Power Status is [ %s ]", ( powerStatus ? "ON" : "OFF" ) ) );
                return powerStatus;
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get Server Power Status
     * 
     * @param node
     * @param connector
     * @return boolean
     */
    @Override
    public boolean getServerPowerStatus( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        boolean powerStatus;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            PowerStatusServerTask powerStatusTask = new PowerStatusServerTask( node, connector );
            powerStatus = powerStatusTask.executeTask();
            logger.info( "Power status obtained for node: " + node.getNodeID() );
            return powerStatus;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to perform Chassis identification via some kind of mechanism, such as Blinking lights, Beeps
     * 
     * @param node
     * @param chassisIdentifyOptions
     * @return boolean
     */
    @Override
    public boolean performChassisIdentification( ServiceHmsNode node, ChassisIdentifyOptions chassisIdentifyOptions )
        throws Exception
    {
        if ( node != null && node instanceof ServiceServerNode && chassisIdentifyOptions != null )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to get Chassis Identification for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                boolean status = performChassisIdentification( node, connector, chassisIdentifyOptions );
                logger.info( String.format( "Chassis Identification is [ %s ]", ( status ? "ON" : "OFF" ) ) );
                return status;
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
        }
        else
        {
            String err =
                String.format( "Node is [ %s ] or not ServiceServerNode and ChassisIdentifyOptions Object is [ %s ]. ",
                               node, chassisIdentifyOptions );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to perform Chassis identification via some kind of mechanism, such as Blinking lights, Beeps
     * 
     * @param node
     * @param chassisIdentifyOptions
     * @param connector
     * @return boolean
     */
    @Override
    public boolean performChassisIdentification( ServiceHmsNode node, IpmiTaskConnector connector,
                                                 ChassisIdentifyOptions chassisIdentifyOptions )
                                                     throws Exception
    {
        if ( node != null && node instanceof ServiceServerNode && connector != null && chassisIdentifyOptions != null )
        {
            ChassisIdentifyTask chassisIdentifyTask =
                new ChassisIdentifyTask( node, connector, chassisIdentifyOptions );
            boolean status = chassisIdentifyTask.executeTask();
            logger.info( "Chassis Identification obtained for node: " + node.getNodeID() );
            return status;
        }
        else
        {
            String err =
                String.format( "Node is [ %s ] or not ServiceServerNode and Connector object is [ %s ] and ChassisIdentifyOptions Object is [ %s ]. ",
                               node, connector, chassisIdentifyOptions );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get the System Event Log (SEL) Information like number of total entries, last addition date and last
     * erase date etc.
     * 
     * @param node
     * @return SelInfo
     */
    @Override
    public SelInfo getSelInfo( ServiceHmsNode node )
        throws Exception
    {
        SelInfo selInfo;
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to get System Event Log (SEL) info for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                selInfo = getSelInfo( serviceServerNode, connector );
                logger.info( "SEL Info retrieved." );
                return selInfo;
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get the System Event Log (SEL) Information like number of total entries, last addition date and last
     * erase date etc.
     * 
     * @param node
     * @param connector
     * @return SelInfo
     */
    @Override
    public SelInfo getSelInfo( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        SelInfo selInfo;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            SelInfoTask selInfoTask =
                new SelInfoTask( node, connector, SelTask.SelInfo, Constants.DEFAULT_MAX_SEL_COUNT,
                                 SelFetchDirection.RecentEntries );
            selInfo = selInfoTask.executeTask();
            logger.info( "SEL info obtained for node: " + node.getNodeID() );
            return selInfo;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get the System Event Log (SEL) Information like number of total entries, last addition date and last
     * erase date etc. along with List of Sel Records.
     * 
     * @param node
     * @return SelInfo
     */
    @Override
    public SelInfo getSelDetails( ServiceHmsNode node )
        throws Exception
    {
        return getSelDetails( node, null );
    }

    /**
     * Method to get the System Event Log (SEL) Information like number of total entries, last addition date and last
     * erase date etc. along with List of Sel Records.
     * 
     * @param node
     * @param selFilters
     * @return SelInfo
     */
    @Override
    public SelInfo getSelDetails( ServiceHmsNode node, List<SelRecord> selFilters )
        throws Exception
    {
        return getSelDetails( node, Constants.DEFAULT_MAX_SEL_COUNT, SelFetchDirection.RecentEntries, selFilters );
    }

    @Override
    public SelInfo getSelDetails( ServiceHmsNode node, Integer recordCount, SelFetchDirection direction )
        throws Exception
    {
        return getSelDetails( node, recordCount, direction, null );
    }

    /**
     * Method to get the System Event Log (SEL) Information like number of total entries, last addition date and last
     * erase date etc. along with List of Sel Records.
     * 
     * @param node
     * @param direction
     * @param selFilters
     * @return SelInfo
     */
    @Override
    public SelInfo getSelDetails( ServiceHmsNode node, SelFetchDirection direction, List<SelRecord> selFilters )
        throws Exception
    {
        return getSelDetails( node, Constants.DEFAULT_MAX_SEL_COUNT, direction, selFilters );
    }

    /**
     * Method to get the System Event Log (SEL) Information like number of total entries, last addition date and last
     * erase date etc. along with List of Sel Records.
     * 
     * @param node
     * @param recordCount
     * @param direction
     * @param selFilters
     * @return SelInfo
     */
    @Override
    public SelInfo getSelDetails( ServiceHmsNode node, Integer recordCount, SelFetchDirection direction,
                                  List<SelRecord> selFilters )
                                      throws Exception
    {
        SelInfo selInfo;
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Received request to get System Event Log (SEL) Details for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                if ( recordCount == null )
                {
                    recordCount = Constants.DEFAULT_MAX_SEL_COUNT;
                }
                if ( direction == null )
                {
                    direction = SelFetchDirection.RecentEntries;
                }
                selInfo = getSelDetails( serviceServerNode, connector, recordCount, direction, selFilters );
                logger.info( "SEL Details retrieved." );
                return selInfo;
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to get the System Event Log (SEL) Information like number of total entries, last addition date and last
     * erase date etc. along with List of Sel Records.
     * 
     * @param node
     * @param connector
     * @param recordCount
     * @param direction
     * @param selFilters
     * @return SelInfo
     */
    @Override
    public SelInfo getSelDetails( ServiceHmsNode node, IpmiTaskConnector connector, Integer recordCount,
                                  SelFetchDirection direction, List<SelRecord> selFilters )
                                      throws Exception
    {
        SelInfo selInfo;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            if ( recordCount == null )
            {
                recordCount = Constants.DEFAULT_MAX_SEL_COUNT;
            }
            if ( direction == null )
            {
                direction = SelFetchDirection.RecentEntries;
            }
            SelInfoTask selInfoTask = new SelInfoTask( node, connector, SelTask.SelDetails, recordCount, direction );
            if ( selFilters != null && !selFilters.isEmpty() )
            {
                selInfo = selInfoTask.executeTaskWithFilter( selFilters );
            }
            else
            {
                selInfo = selInfoTask.executeTask();
            }
            logger.info( "SEL Details obtained for node: " + node.getNodeID() );
            return selInfo;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to check whether we are able to reach to node or not
     * 
     * @param node
     * @return boolean
     */
    @Override
    public boolean isHostAvailable( ServiceServerNode node )
        throws Exception
    {
        boolean status;
        if ( node instanceof ServiceServerNode )
        {
            ServiceServerNode serviceServerNode = null;
            IpmiTaskConnector connector = null;
            logger.debug( "Is the node/host available or reachable for Node: " + node.getNodeID() );
            try
            {
                serviceServerNode = (ServiceServerNode) node;
                connector = getIpmiTaskConnector( serviceServerNode );
                status = isHostAvailable( node, connector );
                logger.info( String.format( "Is the host available: [ %s ]", ( status ? "ON" : "OFF" ) ) );
                return status;
            }
            finally
            {
                logger.debug( "Destroy the Ipmi task connector" );
                returnIpmiTaskConnector( (ServiceServerNode) node, connector );
            }
        }
        else
        {
            String err = String.format( NULL_NODE_EXCEPTION_MSG, node );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }

    /**
     * Method to check whether we are able to reach to node or not
     * 
     * @param node
     * @param connector
     * @return boolean
     */
    @Override
    public boolean isHostAvailable( ServiceHmsNode node, IpmiTaskConnector connector )
        throws Exception
    {
        boolean status;
        if ( node instanceof ServiceServerNode )
        {
            IsHostAvailableTask isHostAvailableTask = new IsHostAvailableTask( node, connector );
            status = isHostAvailableTask.executeTask();
            logger.info( "Is host available/reachable - for node: " + node.getNodeID() );
            return status;
        }
        else
        {
            String err = String.format( NULL_NODE_OR_CONNECTOR_EXCEPTION_MSG, node, connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }
}
