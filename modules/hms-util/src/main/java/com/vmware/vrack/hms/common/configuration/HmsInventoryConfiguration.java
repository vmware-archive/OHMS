/* ********************************************************************************
 * HmsInventoryConfiguration.java
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
package com.vmware.vrack.hms.common.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HmsException;

/**
 * The Class HmsInventoryConfiguration.
 */
@JsonInclude( JsonInclude.Include.NON_NULL )
@JsonPropertyOrder( { "name", "version", "servers", "switches" } )
public class HmsInventoryConfiguration
{

    /** The name. */
    private String name;

    /** The version. */
    private String version;

    /** The filename. */
    private String filename;

    /** The servers. */
    private List<ServerItem> servers;

    /** The switches. */
    private List<SwitchItem> switches;

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( HmsInventoryConfiguration.class );

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version the new version
     */
    public void setVersion( String version )
    {
        this.version = version;
    }

    /**
     * Gets the filename.
     *
     * @return the filename
     */
    @JsonIgnore
    public String getFilename()
    {
        return filename;
    }

    /**
     * Sets the filename.
     *
     * @param filename the new filename
     */
    public void setFilename( String filename )
    {
        this.filename = filename;
    }

    /**
     * Gets the servers.
     *
     * @return the servers
     */
    public List<ServerItem> getServers()
    {
        return servers;
    }

    /**
     * Sets the servers.
     *
     * @param servers the new servers
     */
    public void setServers( List<ServerItem> servers )
    {
        this.servers = servers;
    }

    /**
     * Gets the switches.
     *
     * @return the switches
     */
    public List<SwitchItem> getSwitches()
    {
        return switches;
    }

    /**
     * Sets the switches.
     *
     * @param switches the new switches
     */
    public void setSwitches( List<SwitchItem> switches )
    {
        this.switches = switches;
    }

    /**
     * Load.
     *
     * @param fileName the file name
     * @return the hms inventory configuration
     * @throws HmsException the hms exception
     */
    public static HmsInventoryConfiguration load( String fileName )
        throws HmsException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        HmsInventoryConfiguration hic = new HmsInventoryConfiguration();
        try
        {
            hic = objectMapper.readValue( new File( fileName ), HmsInventoryConfiguration.class );
            hic.setFilename( fileName );
        }
        catch ( IOException e )
        {
            logger.error( "Error loading HMS inventory configuration file: {}.", fileName, e );
            throw new HmsException( "Error loading HMS inventory configuration file: " + fileName, e );
        }
        return hic;
    }

    /**
     * Reload.
     *
     * @throws HmsException the hms exception
     */
    public void reload()
        throws HmsException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader objectReader = objectMapper.readerForUpdating( this );
        try
        {
            objectReader.readValue( new File( filename ) );
        }
        catch ( IOException e )
        {
            logger.error( "Error reloading HMS inventory configuration file: {}.", filename, e );
            throw new HmsException( "Error reloading HMS inventory configuration file: " + filename, e );
        }
    }

    /**
     * Store.
     *
     * @param filename the filename
     * @throws HmsException the hms exception
     */
    public void store( String filename )
        throws HmsException
    {
        store( filename, false );
    }

    /**
     * Store: Save the file only if it exists
     *
     * @param filename the filename
     * @param backup the backup
     * @throws HmsException the hms exception
     */
    public void store( String filename, boolean backup )
        throws HmsException
    {
        HmsInventoryConfiguration.store( filename, backup, this );
    }

    /**
     * Saves HmsInventoryConfiguration as the given fileName. Backup of the file will be created, if backup is true, and
     * the file exists already.
     *
     * @param fileName the file name
     * @param backup the backup
     * @param hic the hic
     * @return true, if successful
     * @throws HmsException the hms exception
     */
    public static boolean store( final String fileName, final boolean backup, final HmsInventoryConfiguration hic )
        throws HmsException
    {

        // Check that file name is not blank and inventory object is not null.
        if ( StringUtils.isBlank( fileName ) || hic == null )
        {
            logger.warn( "In store, either file name is null or blank or HmsInventoryConfiguration is null." );
            return false;
        }

        File file = new File( fileName );
        if ( !file.exists() )
        {
            logger.warn( "In store, inventory file '{}' does not exist. Not saving it.", fileName );
            return false;
        }

        // Create backup, if file exists and backup is required.
        if ( backup && file.exists() )
        {
            try
            {
                String backupfile = file.getName() + ".bak." + file.lastModified();
                File b = new File( file.getParent(), backupfile );
                logger.debug( "In store, saving backup of HMS inventory file as {}.", b.getAbsolutePath() );
                Files.copy( file.toPath(), b.toPath(), StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES );
            }
            catch ( IOException ioe )
            {
                logger.warn( "Error saving backup copy of file {}.", file.getAbsolutePath(), ioe );
                // Still proceed if backup fails.
            }
        }

        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure( SerializationFeature.INDENT_OUTPUT, true );
            objectMapper.writeValue( file, hic );
            return true;
        }
        catch ( IOException e )
        {
            logger.error( "In store, error saving inventory to file '{}'.", fileName, e );
            throw new HmsException( String.format( "Error storing HMS inventory to file '%s'", fileName ), e );
        }
    }

    /**
     * Removes the server.
     *
     * @param hostId the host id
     * @return true, if successful
     * @throws HmsException
     */
    public boolean removeServer( final String hostId )
        throws HmsException
    {
        if ( this.servers == null || this.servers.isEmpty() )
        {
            logger.debug( "In removeServer, either servers is null or is empty." );
            return false;
        }
        ServerItem serverItem = null;
        for ( Iterator<ServerItem> serverItemIterator = this.servers.iterator(); serverItemIterator.hasNext(); )
        {
            serverItem = serverItemIterator.next();
            if ( StringUtils.equals( serverItem.getId(), hostId ) )
            {
                serverItemIterator.remove();
                logger.debug( "In removeServer, removed hostId '{}' from the servers list.", hostId );

                // Below to remove the server from inventory file as well
                String invConfFile = HmsConfigHolder.getInventoryConfigFileName();
                store( invConfFile, true );
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the server.
     *
     * @param serverItem the server item
     * @return true, if successful
     */
    public boolean addServer( ServerItem serverItem )
    {
        if ( this.servers == null )
        {
            logger.debug( "In addServer, servers is null." );
            return false;
        }
        if ( serverItem == null || StringUtils.isBlank( serverItem.getId() ) )
        {
            logger.debug( "In addServer, either serverItem is null or serverItem ID is null or blank." );
            return false;
        }
        return this.servers.add( serverItem );
    }

    /**
     * Parses the given inventory as HmsInventoryConfiguration.class type. If the inventory is successfully parsed as
     * HmsInventoryConfiguration.class type, an instance of HmsInventoryConfiguration will be returned. Otherwise, null
     * will be returned.
     *
     * @param inventory the inventory
     * @return the hms inventory configuration
     */
    public static HmsInventoryConfiguration getHmsInventoryConfiguration( final String inventory )
    {

        // Check that inventory is not null or blank.
        if ( StringUtils.isBlank( inventory ) )
        {
            logger.warn( "In getHmsInventoryConfiguration, inventory is either null or blank." );
            return null;
        }

        logger.debug( "In getHmsInventoryConfiguration, parsing inventory as HmsInventoryConfiguration.class type." );
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            return objectMapper.readValue( inventory, new TypeReference<HmsInventoryConfiguration>()
            {
            } );
        }
        catch ( IOException e )
        {
            logger.error( "In getHmsInventoryConfiguration, error while parsing inventory "
                + "as HmsInventoryConfiguration.class type.", e );
        }
        return null;
    }
}
