/* ********************************************************************************
 * PropertiesHolder.java
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
package com.vmware.vrack.hms.common;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Properties Holder to load custom Properties file for several components of App, This will try to load file by
 * filepath first, if fails, then it will try searching within classpath
 * 
 * @author VMware, Inc.
 */
public class PropertiesHolder
{
    private Properties properties = new Properties();;

    private String fileName;

    private static Logger logger = Logger.getLogger( PropertiesHolder.class );

    private PropertiesHolder()
    {
    }

    /**
     * @param fileName
     */
    public PropertiesHolder( String fileName )
    {
        super();
        if ( fileName != null && !"".equals( fileName.trim() ) )
        {
            this.fileName = fileName;
            final Path path = Paths.get( this.fileName );
            try
            {
                if ( Files.exists( path, LinkOption.NOFOLLOW_LINKS ) )
                {
                    logger.debug( "Loading properties from File Path" );
                    properties.load( new FileInputStream( this.fileName ) );
                }
                else
                {
                    // Otherwise, use resource as stream.
                    // ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    // properties.load(loader.getResourceAsStream(this.fileName));
                    logger.debug( "Loading properties from ClassPath" );
                    properties.load( this.getClass().getClassLoader().getResourceAsStream( this.fileName ) );
                }
            }
            catch ( Exception e )
            {
                logger.error( "Recieved exception while loading Properties file ", e );
            }
            Properties systemProperties = System.getProperties();
            for ( Enumeration<Object> e = systemProperties.keys(); e.hasMoreElements(); )
            {
                String key = e.nextElement().toString();
                if ( properties.containsKey( key ) )
                {
                    String value = systemProperties.getProperty( key ).toString();
                    properties.put( key, value );
                }
            }
        }
    }

    public Properties getProperties()
    {
        return properties;
    }

    public void setProperties( Properties appProperties )
    {
        this.properties = appProperties;
    }

    public String getProperty( String propertyName )
    {
        if ( propertyName != null && properties != null )
        {
            return properties.getProperty( propertyName );
        }
        return null;
    }
}
