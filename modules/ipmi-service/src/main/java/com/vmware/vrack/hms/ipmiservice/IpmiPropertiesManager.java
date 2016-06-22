/* ********************************************************************************
 * IpmiPropertiesManager.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.ipmiservice;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.common.PropertiesManager;

public class IpmiPropertiesManager
{
    private static IpmiPropertiesManager instance;

    private Map<String, String> properties;

    private Logger logger = Logger.getLogger( PropertiesManager.class );

    // TODO : PASS APP CONFIG FILE WHILE INSTANTIATING LIBRARY
    private static final String APP_CONFIG_FILE = "config/connection.properties";

    private static final String DEFAULT_CONFIG_FILE = "/connection.properties";

    private IpmiPropertiesManager()
    {
        properties = new HashMap<String, String>();
        loadProperties();
    }

    public static IpmiPropertiesManager getInstance()
    {
        if ( instance == null )
        {
            instance = new IpmiPropertiesManager();
        }
        return instance;
    }

    private void loadProperties()
    {
        final Path path = Paths.get( APP_CONFIG_FILE );
        Properties properties = new Properties();
        try
        {
            if ( Files.exists( path, LinkOption.NOFOLLOW_LINKS ) )
            {
                logger.debug( "Loading properties from File Path" );
                properties.load( new FileInputStream( path.toString() ) );
            }
            else
            {
                // Otherwise, use resource as stream.
                logger.debug( "Loading properties from ClassPath" );
                properties.load( this.getClass().getResourceAsStream( DEFAULT_CONFIG_FILE ) );
            }
            for ( Object key : properties.keySet() )
            {
                this.properties.put( key.toString(), properties.getProperty( key.toString() ) );
            }
        }
        catch ( IOException e )
        {
            logger.error( e.getMessage(), e );
        }
    }

    public String getProperty( String key )
    {
        logger.info( "Getting " + key + ": " + properties.get( key ) );
        return properties.get( key );
    }

    public void setProperty( String key, String value )
    {
        properties.put( key, value );
    }
}
