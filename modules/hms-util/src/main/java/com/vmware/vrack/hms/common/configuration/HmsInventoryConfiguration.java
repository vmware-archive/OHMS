/*
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/* ********************************************************************************
 * HmsInventoryConfiguration.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vrack.hms.common.exception.HmsException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

@JsonInclude( JsonInclude.Include.NON_NULL )
@JsonPropertyOrder( { "name", "version", "servers", "switches" } )
public class HmsInventoryConfiguration
{
    private static Logger logger = Logger.getLogger( HmsInventoryConfiguration.class );

    private String name;

    private String version;

    private String filename;

    private List<ServiceItem> services;

    private List<ServerItem> servers;

    private List<SwitchItem> switches;

    public static HmsInventoryConfiguration load( String filename )
        throws HmsException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        HmsInventoryConfiguration hic = new HmsInventoryConfiguration();
        try
        {
            hic = objectMapper.readValue( new File( filename ), HmsInventoryConfiguration.class );
            hic.setFilename( filename );
        }
        catch ( IOException e )
        {
            logger.error( "Error loading HMS inventory configuration file " + filename, e );
            throw new HmsException( "Error loading HMS inventory configuration file " + filename, e );
        }
        return hic;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    @JsonIgnore
    public String getFilename()
    {
        return filename;
    }

    public void setFilename( String filename )
    {
        this.filename = filename;
    }

    public List<ServiceItem> getServices()
    {
        return services;
    }

    public void setServices( List<ServiceItem> services )
    {
        this.services = services;
    }

    public List<ServerItem> getServers()
    {
        return servers;
    }

    public void setServers( List<ServerItem> servers )
    {
        this.servers = servers;
    }

    public List<SwitchItem> getSwitches()
    {
        return switches;
    }

    public void setSwitches( List<SwitchItem> switches )
    {
        this.switches = switches;
    }

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
            logger.error( "Error reloading HMS inventory configuration file " + filename, e );
            throw new HmsException( "Error reloading HMS inventory configuration file " + filename, e );
        }
    }

    public void store( String filename )
        throws HmsException
    {
        store( filename, false );
    }

    public void store( String filename, boolean backup )
        throws HmsException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable( SerializationFeature.INDENT_OUTPUT );
        File file = new File( filename );
        if ( backup && file.exists() )
        {
            try
            {
                String backupFile = file.getName() + ".bak." + file.lastModified();
                File b = new File( file.getParent(), backupFile );
                logger.debug( "Saving backup of HMS inventory file to " + b.getAbsolutePath() );
                Files.copy( file.toPath(), b.toPath(), StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES );
            }
            catch ( IOException ioe )
            {
                logger.warn( "Error saving backup copy of file " + file.getAbsolutePath(), ioe );
                /* Still proceed if backup fails. */
            }
        }
        try
        {
            objectMapper.writeValue( file, this );
        }
        catch ( IOException e )
        {
            logger.error( "Error storing HMS inventory configuration file " + filename, e );
            throw new HmsException( "Error storing HMS inventory configuration file " + filename, e );
        }
    }
}
