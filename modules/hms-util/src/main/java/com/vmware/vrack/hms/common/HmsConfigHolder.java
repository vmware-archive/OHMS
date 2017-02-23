/* ********************************************************************************
 * HmsConfigHolder.java
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
package com.vmware.vrack.hms.common;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.common.configuration.HmsInventoryConfiguration;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.util.CommonProperties;
import com.vmware.vrack.hms.common.util.Constants;

/**
 * Class to facilitate the initialization of different components of hms Application
 *
 * @author Yagnesh Chawda
 */
public class HmsConfigHolder
{

    private static Logger logger = LoggerFactory.getLogger( HmsConfigHolder.class );

    public static final String HMS_CONFIG_PROPS = "HMS_CONFIG_PROPS";

    public static final String SWITCH_CONFIG_PROPS = "SWITCH_CONFIG_PROPS";

    public static final String SERVER_CONFIG_PROPS = "SERVER_CONFIG_PROPS";

    public static final String HMS_PROXY_PROPS = "HMS_PROXY_PROPS";

    public static final String HMS_NETWORK_CONFIGURATIONS_DIRECTORY = "hms.network.configurations.directory";

    public static final String HMS_RESOURCE_MONITOR_DISABLE_RESTARTS = "hms.resource.monitor.disable.restarts";

    public static final String HMS_SWITCH_CONNECTION_TIMEOUT = "hms.switch.connection.timeout";

    public static final String HMS_NODE_DISCOVERY_REATTEMPTS = "hms.discovery.reattempts";

    public static final String HMS_NODE_DISCOVERY_REATTEMPT_WAIT = "hms.discovery.reattempt.wait";

    public static final String IPMI_CONFIG = "ipmi.config.file";

    public static final String HMS_SERVICE_MAINTENANCE_MAX_WAIT_TIME = "hms.service.maintenance.max-wait-time";

    public static final String HMS_SERVICE_MAINTENANCE_RETRY_INTERVAL = "hms.service.maintenance.retry-interval";

    public static final String HMS_LOG_FILE_PATH = "hms.log.path";

    public static final String HMS_LOG_1_FILE_PATH = "hms.log1.path";

    public static final String HMS_TEMPORARY_LOG_DIR = "hms.temporary.log.directory";

    public static final String HMS_TEMPORARY_LOG_FILE_CLEAR_DURATION = "hms.temporary.log.file.clear.duration";

    public static final String MAX_CONCURRENT_TASKS_PER_NODE_KEY = "hms.max.concurrent.tasks.per.node";

    public static final String HMS_INVENTORY_CONFIGURATION_FILE = "hms.inventory.configuration.file";

    public static final String DEFAULT_HMS_INVENTORY_CONFIGURATION_FILE = "config/hms-inventory.json";

    public static final String HMS_INVENTORY_RELOAD_DELAY = "hms.inventory.reload.delay";

    public static final String HMS_AGGREGATOR_DEFAULT_IP = "hms.aggregator.defaultip";

    public static final String HMS_PROXY_CONFIG_FILE = "hms.proxy.config.file";

    public static final String HMS_OOB_ALLOWED_PRIMARY_IP = "hms.oob.allowed.primary.ip";

    public static final String HMS_OOB_ALLOWED_SECONDARY_IP = "hms.oob.allowed.secondary.ip";

    public static final String HMS_IP_REACHABILITY_VERIFICATION_COMMAND = "hms.host.reachability.verification.command";

    private static Map<String, PropertiesHolder> props = new HashMap<String, PropertiesHolder>();

    private static HmsInventoryConfiguration hmsInventoryConfiguration = null;

    // Below flag is the indicator if the inventory is pushed from Aggregator or
    // not.
    private static boolean isInvRefreshedFromAggregator = false;

    /**
     * Function to get a particular property out of all properties
     *
     * @param propHolder
     * @param property
     * @return
     */
    public static String getProperty( String propHolderKey, String property )
    {
        if ( propHolderKey != null && property != null )
        {

            PropertiesHolder propertyHolder = props.get( propHolderKey );
            if ( propertyHolder != null )
            {

                Properties properties = propertyHolder.getProperties();
                return properties.getProperty( property );
            }
        }

        return null;
    }

    /**
     * Function to set a particular property on oob agent properties file
     *
     * @param propHolder
     * @param property
     * @return
     * @throws HmsException
     */
    public synchronized static void setProperty( String propHolderKey, String property, String value )
        throws HmsException
    {
        Lock lock = new ReentrantLock();
        lock.lock();
        try
        {
            if ( propHolderKey != null && property != null && value != null )
            {

                PropertiesHolder propertyHolder = props.get( propHolderKey );
                if ( propertyHolder != null )
                {
                    Properties properties = propertyHolder.getProperties();
                    properties.setProperty( property, value );
                    logger.debug( "{} is saved to in-memory props", property );
                }

                try
                {
                    PropertiesConfiguration propConfig = new PropertiesConfiguration( getFileName() );
                    propConfig.setProperty( property, value );
                    propConfig.save();
                    logger.debug( "{} is saved to the file: {}", property, getFileName() );
                }
                catch ( Exception e )
                {
                    String message = String.format( "Unable to modify the property: %s, and the exception " + "is: %s",
                                                    property, e );
                    logger.error( message );
                    throw new HmsException( message );
                }
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    public static Properties getProperties( String propHolderKey )
    {

        if ( propHolderKey != null && props.get( propHolderKey ) != null )
        {
            Properties properties = props.get( propHolderKey ).getProperties();
            return properties;
        }
        return null;
    }

    public static String getHMSConfigProperty( String property )
    {
        PropertiesHolder propertyHolder = props.get( HMS_CONFIG_PROPS );
        if ( propertyHolder != null )
        {
            Properties properties = propertyHolder.getProperties();
            return properties.getProperty( property );
        }
        return null;
    }

    /**
     * Initializes Hms App Properties
     */
    public static void initializeHmsAppProperties()
    {

        initializeHmsProperties();
        initializeCommonProperties();
        // initializeServerProperties();
        // initializeSwichProperties();
    }

    public static void initializeCommonProperties()
    {

        logger.debug( "Initilizing HMS Common properties." );
        CommonProperties hmsCommonProperties = new CommonProperties();

        /*
         * hmsCommonProperties.setPluginThreadPoolCount(Integer
         * .parseInt(getHMSConfigProperty("hms.task.scheduler.thread.count")));
         * hmsCommonProperties.setPluginTaskTimeOut(Long.parseLong( getHMSConfigProperty("hms.plugin.task.timeout")));
         */
        hmsCommonProperties.setMaxConcurrentTasksPerNode( Integer.parseInt( getHMSConfigProperty( MAX_CONCURRENT_TASKS_PER_NODE_KEY ) ) );
        hmsCommonProperties.setConcurrentOperationRetryThreadSleepTime( Long.parseLong( getHMSConfigProperty( "hms.node.concurrent.operation.retry.thread.sleep.time" ) ) );
        logger.debug( "Initialized HMS Common properties." );
    }

    /**
     * Initializes PropertiesHolder which will load properties from the given filepath
     *
     * @param key
     * @param filePath
     */
    public static void initializePropertiesHolder( String key, String filePath )
    {

        if ( key != null && filePath != null )
        {

            props.put( key, new PropertiesHolder( filePath ) );
        }
    }

    /**
     * Initializes HmsProperties
     */
    public static void initializeHmsProperties()
    {

        String fileName = getFileName();

        initializePropertiesHolder( HMS_CONFIG_PROPS, fileName );

        PropertiesHolder hmsConfig = props.get( HMS_CONFIG_PROPS );

        Field[] fields = Constants.class.getFields();
        try
        {

            Properties props = hmsConfig.getProperties();

            for ( int i = 0; i < fields.length; i++ )
            {
                if ( Modifier.isPublic( fields[i].getModifiers() ) && !fields[i].getType().isArray() )
                {

                    if ( !props.containsKey( fields[i].getName() ) )
                    {
                        String key = fields[i].getName();
                        String value = fields[i].get( fields[i].getName() ).toString();
                        props.put( key, value );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            logger.error( "Received Exception while populating Properties object from Constants file, exception: {}",
                          e );
        }

    }

    private static String getFileName()
    {
        String fileName;
        if ( System.getProperty( "hms.config.file" ) != null )
        {

            fileName = System.getProperty( "hms.config.file" );

        }
        else
        {

            fileName = Constants.HMS_DEFAULT_CONFIG_FILE;
        }
        return fileName;
    }

    /**
     * initializes Server Properties
     */
    public static void initializeServerProperties()
    {

    }

    /**
     * Initializes Switch Properties
     */
    public static void initializeSwitchProperties()
    {

    }

    /**
     * This method parses the HMS inventory configuration file and returns an object that represents it.
     *
     * @return HmsInventoryConfiguration object
     */
    public synchronized static HmsInventoryConfiguration getHmsInventoryConfiguration()
        throws HmsException
    {

        if ( hmsInventoryConfiguration == null )
        {
            hmsInventoryConfiguration = forceLoadHmsInventoryConfiguration();
        }

        return hmsInventoryConfiguration;
    }

    public static boolean isHmsInventoryConfigLoadedInlineMemory()
    {
        if ( hmsInventoryConfiguration == null )
        {
            return false;
        }
        return true;
    }

    /**
     * Returns true if the Hms inventory config file is available in classpath, or else returns false.
     *
     * @return
     */
    public synchronized static boolean isHmsInventoryFileExists()
    {
        String invFilename = HmsConfigHolder.getInventoryConfigFileName();
        Path path = Paths.get( invFilename );
        if ( Files.exists( path ) )
        {
            return true;
        }
        return false;
    }

    /**
     * This method parses the HMS inventory configuration file and returns an object that represents it.
     *
     * @return HmsInventoryConfiguration object
     */
    public synchronized static HmsInventoryConfiguration forceLoadHmsInventoryConfiguration()
        throws HmsException
    {

        String invFilename = HmsConfigHolder.getInventoryConfigFileName();
        return HmsInventoryConfiguration.load( invFilename );
    }

    /**
     * This method updates the HMS inventory configuration file with the object parameter.
     *
     * @param HmsInventoryConfiguration
     */
    public synchronized static void setHmsInventoryConfiguration( HmsInventoryConfiguration hic )
        throws HmsException
    {

        if ( hic != null )
        {
            String invFilename = HmsConfigHolder.getInventoryConfigFileName();
            logger.debug( "Updating HMS inventory configuration file located at {}.", invFilename );
            hic.store( invFilename, true );
            hmsInventoryConfiguration = hic;
        }
    }

    /**
     * Gets the inventory config file name.
     *
     * @return the inventory config file name
     */
    public static String getInventoryConfigFileName()
    {
        String invConfigPropValue = getHMSConfigProperty( HMS_INVENTORY_CONFIGURATION_FILE );
        String invConfigFileName =
            ( invConfigPropValue != null ) ? invConfigPropValue : DEFAULT_HMS_INVENTORY_CONFIGURATION_FILE;
        return invConfigFileName;
    }

    /**
     * Method to set a particular property on in-line memory and file which is parameterized. This will not escape the
     * characters. As the proxy doesn't accept escaped characters need to go with this. This method uses
     * {@link Properties} instead of {@link PropertiesConfiguration}
     *
     * @param filePath
     * @param propHolderKey
     * @param property
     * @param value
     * @throws HmsException
     */
    public static void setProperty( String filePath, String propHolderKey, String property, String value )
        throws HmsException
    {
        Lock lock = new ReentrantLock();
        lock.lock();
        try
        {
            if ( propHolderKey != null && property != null && value != null )
            {

                HmsConfigHolder.initializePropertiesHolder( propHolderKey, filePath );
                PropertiesHolder propertyHolder = props.get( propHolderKey );
                Properties properties = propertyHolder.getProperties();

                if ( propertyHolder != null )
                {
                    properties.setProperty( property, value );
                    logger.debug( "{} is saved to in memory props", property );
                }
                try
                {
                    OutputStream output = new FileOutputStream( filePath );
                    properties.store( output, null );
                    logger.debug( "{} is saved to the file: {}", property, filePath );
                }
                catch ( Exception e )
                {
                    String message =
                        String.format( "Unable to modify the property: %s, and the exception is: %s", property, e );
                    logger.error( message );
                    throw new HmsException( message );
                }
            }
        }
        finally
        {
            lock.unlock();
        }

    }

    public static boolean isInvRefreshedFromAggregator()
    {
        return isInvRefreshedFromAggregator;
    }

    public static void setInvRefreshedFromAggregator( boolean isInvRefreshedFromAggregator )
    {
        HmsConfigHolder.isInvRefreshedFromAggregator = isInvRefreshedFromAggregator;
    }
}
