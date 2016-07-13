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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

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
    private static Logger logger = Logger.getLogger( HmsConfigHolder.class );

    public static final String HMS_CONFIG_PROPS = "HMS_CONFIG_PROPS";

    public static final String SWITCH_CONFIG_PROPS = "SWITCH_CONFIG_PROPS";

    public static final String SERVER_CONFIG_PROPS = "SERVER_CONFIG_PROPS";

    public static final String HMS_NETWORK_CONFIGURATIONS_DIRECTORY = "hms.network.configurations.directory";

    public static final String HMS_INVENTORY_CONFIGURATION_FILE = "hms.inventory.configuration.file";

    public static final String HMS_RESOURCE_MONITOR_DISABLE_RESTARTS = "hms.resource.monitor.disable.restarts";

    public static final String HMS_SWITCH_CONNECTION_TIMEOUT = "hms.switch.connection.timeout";

    public static final String DEFAULT_HMS_INVENTORY_CONFIGURATION_FILE = "config/hms-inventory.json";

    public static final String HMS_NODE_DISCOVERY_REATTEMPTS = "hms.discovery.reattempts";

    public static final String HMS_NODE_DISCOVERY_REATTEMPT_WAIT = "hms.discovery.reattempt.wait";

    public static final String IPMI_CONFIG = "ipmi.config.file";

    public static final String HMS_SERVICE_MAINTENANCE_MAX_WAIT_TIME = "hms.service.maintenance.max-wait-time";

    public static final String HMS_SERVICE_MAINTENANCE_RETRY_INTERVAL = "hms.service.maintenance.retry-interval";

    private static Map<String, PropertiesHolder> props = new HashMap<String, PropertiesHolder>();

    private static HmsInventoryConfiguration hmsInventoryConfiguration = null;

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
        hmsCommonProperties.setPluginThreadPoolCount( Integer.parseInt( getHMSConfigProperty( "hms.task.scheduler.thread.count" ) ) );
        hmsCommonProperties.setPluginTaskTimeOut( Long.parseLong( getHMSConfigProperty( "hms.plugin.task.timeout" ) ) );
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
        String fileName = null;
        if ( System.getProperty( "hms.config.file" ) != null )
        {
            fileName = System.getProperty( "hms.config.file" );
        }
        else
        {
            fileName = Constants.HMS_DEFAULT_CONFIG_FILE;
        }
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
            logger.error( "Received Exception while populating Properties object from Constants file ", e );
        }
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
            String prop = getHMSConfigProperty( HMS_INVENTORY_CONFIGURATION_FILE );
            String invFilename = ( prop != null ) ? prop : DEFAULT_HMS_INVENTORY_CONFIGURATION_FILE;
            hmsInventoryConfiguration = HmsInventoryConfiguration.load( invFilename );
        }
        return hmsInventoryConfiguration;
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
            String prop = getHMSConfigProperty( HMS_INVENTORY_CONFIGURATION_FILE );
            String invFilename = ( prop != null ) ? prop : DEFAULT_HMS_INVENTORY_CONFIGURATION_FILE;
            logger.debug( "Updating HMS inventory configuration file located at " + invFilename );
            hic.store( invFilename, true );
            /* Let the file be re-read when requested */
            hmsInventoryConfiguration = null;
        }
    }
}
