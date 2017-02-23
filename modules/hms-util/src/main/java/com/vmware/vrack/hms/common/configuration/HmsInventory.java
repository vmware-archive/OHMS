/* ********************************************************************************
 * HmsInventory.java
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;

/**
 * A single instance of class HmsInventory, that models HMS Aggregator Inventory file hms_ib_inventory.json, saved at
 * ${user.home}/VMware/vRack.
 * <p>
 * It consists of instance methods for loading, reloading inventory file, saving inventory file, returning hosts as List
 * of ServerNode instance, Switches as List of SwitchNode instances, etc.
 * </p>
 */
public class HmsInventory
{

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger( HmsInventory.class );

    /** The Constant HOSTS. */
    private static final String HOSTS = "hosts";

    /** The Constant SWITCHES. */
    private static final String SWITCHES = "switches";

    /** The inventory file. */
    private String inventoryFile = null;

    /** The node map. */
    private ConcurrentHashMap<String, Object[]> nodeMap = null;

    /** The hms inventory. */
    private static HmsInventory hmsInventory = null;

    /** The Constant reentrantLock. */
    private static final Lock reentrantLock = new ReentrantLock( true );

    /**
     * Suppresses default constructor, ensuring non-instantiability.
     */
    private HmsInventory()
    {
    }

    /**
     * Gets the single instance of HmsInventory.
     * <p>
     * Thread safety is guaranteed with Lazy initialization. Synchronization overhead is minimal and applicable only for
     * first few threads when the variable is null
     * </p>
     *
     * @return single instance of HmsInventory
     */
    public static HmsInventory getInstance()
    {
        if ( hmsInventory == null )
        {
            // try updating node map thread safe way
            if ( reentrantLock.tryLock() )
            {
                try
                {
                    hmsInventory = new HmsInventory();
                }
                finally
                {
                    reentrantLock.unlock();
                }
            }
            else
            {
                hmsInventory = new HmsInventory();
            }
        }
        return hmsInventory;
    }

    /**
     * Load inventory.
     *
     * @param inventoryFile the inventory file
     * @return true, if successful
     */
    public boolean loadInventory( final String inventoryFile )
    {

        // check that inventoryFile is not null or blank
        if ( StringUtils.isBlank( inventoryFile ) )
        {
            logger.warn( "In loadInventory, inventory file is either null or blank." );
            return false;
        }

        /*
         * If inventory is already loaded, then inventoryFile will be set to the inventory file from which inventory was
         * loaded. Log a warning message, if the inventoryFile that was already loaded does not match with the
         * inventoryFile that was asked to load inventory from.
         */
        if ( this.inventoryFile != null && StringUtils.equals( this.inventoryFile, inventoryFile ) )
        {
            logger.warn( "In loadInventory, overwriting inventory loaded from '{}' with inventory from '{}'.",
                         this.inventoryFile, inventoryFile );
        }

        try
        {

            ObjectMapper objectMapper = new ObjectMapper();
            nodeMap = objectMapper.readValue( new File( inventoryFile ),
                                              new TypeReference<ConcurrentHashMap<String, Object[]>>()
                                              {
                                              } );

            // set inventory file
            this.inventoryFile = inventoryFile;
            return true;

        }
        catch ( IOException e )
        {
            logger.error( "In loadInventory, error parsing inventory file '{}'.", inventoryFile );
            return false;
        }
    }

    /**
     * Save inventory.
     *
     * @param inventoryFile the inventory file
     * @param backup the backup
     * @return true, if successful
     */
    public boolean saveInventory( final String inventoryFile, boolean backup )
    {

        // check that inventory file to be saved is not null or blank
        if ( StringUtils.isBlank( inventoryFile ) )
        {
            logger.warn( "In saveInventory, inventory file name is either null or blank." );
            return false;
        }

        // take backup of existing file, if backup is true.
        File file = new File( inventoryFile );
        if ( backup && file.exists() )
        {
            try
            {
                String backupfile = file.getName() + ".bak." + file.lastModified();
                File b = new File( file.getParent(), backupfile );
                logger.debug( "In saveInventory, Saving backup of HMS inventory file to {}.", b.getAbsolutePath() );
                Files.copy( file.toPath(), b.toPath(), StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES );
            }
            catch ( IOException ioe )
            {
                logger.warn( "In saveInventory, Error saving backup copy of file {}.", file.getAbsolutePath(), ioe );
            }
        }

        // save inventory
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable( SerializationFeature.INDENT_OUTPUT );
            objectMapper.writeValue( file, this.nodeMap );
            logger.debug( "In saveInventory, saved inventory to file '{}'.", file.getAbsolutePath() );
            return true;
        }
        catch ( IOException e )
        {
            logger.error( "In saveInventory, error while saving inventory to file '{}'.", file.getAbsolutePath() );
            return false;
        }
    }

    /**
     * Save inventory.
     *
     * @param inventoryFile the inventory file
     * @return true, if successful
     */
    public boolean saveInventory( final String inventoryFile )
    {
        return this.saveInventory( inventoryFile, false );
    }

    /**
     * Save inventory.
     *
     * @param backup the backup
     * @return true, if successful
     */
    public boolean saveInventory( final boolean backup )
    {
        return this.saveInventory( this.inventoryFile, backup );
    }

    /**
     * Save inventory.
     *
     * @return true, if successful
     */
    public boolean saveInventory()
    {
        return this.saveInventory( this.inventoryFile, false );
    }

    /**
     * Each host entry in the inventory file is deserialized as ServerNode and then returns a list of ServerNode
     * objects.
     *
     * @return the hosts
     */
    public List<ServerNode> getHosts()
    {

        // check that node map is not null
        if ( nodeMap == null )
        {
            logger.warn( "In getHosts, node map is null. Inventory is not initialized." );
            return null;
        }

        // check that node map contains hosts
        if ( !nodeMap.containsKey( HOSTS ) )
        {
            logger.warn( "In getHosts, node map does not contain '{}'.", HOSTS );
            return null;
        }

        /*
         * Deserialize host list in the inventory as List of ServerNode Instances.
         */
        Object[] hosts = nodeMap.get( HOSTS );
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue( hosts, new TypeReference<List<ServerNode>>()
        {
        } );
    }

    /**
     * Each host entry in the inventory file is deserialized as ServerNode and then returns a list of ServerNode
     * objects.
     *
     * @return the hosts
     */
    public List<SwitchNode> getSwitches()
    {

        // check that node map is not null
        if ( nodeMap == null )
        {
            logger.warn( "In getHosts, node map is null. Inventory is not initialized." );
            return null;
        }

        // check that node map contains switches
        if ( !nodeMap.containsKey( SWITCHES ) )
        {
            logger.warn( "In getHosts, node map does not contain '{}'.", SWITCHES );
            return null;
        }

        /*
         * Deserialize switch list in the inventory as List of SwitchNode instances.
         */
        Object[] switches = nodeMap.get( SWITCHES );
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue( switches, new TypeReference<List<SwitchNode>>()
        {
        } );
    }

    /**
     * Gets the inventory file.
     *
     * @return the inventory file
     */
    public String getInventoryFile()
    {
        return inventoryFile;
    }

    /**
     * Gets the server node.
     *
     * @param hostId the node id
     * @return the server node
     */
    public ServerNode getServerNode( final String hostId )
    {
        // check that nodeId is not null or blank (" ")
        if ( StringUtils.isBlank( hostId ) )
        {
            logger.warn( "In getServerNode, nodeId is either null or blank." );
            return null;
        }
        // get all hosts of the inventory
        List<ServerNode> serverNodes = this.getHosts();
        if ( serverNodes == null || serverNodes.size() == 0 )
        {
            logger.warn( "In getServerNode, failed to get hosts of inventory file '{}'.", this.inventoryFile );
            return null;
        }
        for ( ServerNode serverNode : serverNodes )
        {
            if ( serverNode != null && StringUtils.equals( serverNode.getNodeID(), hostId ) )
            {
                return serverNode;
            }
        }
        return null;
    }

    /**
     * Gets the switch node.
     *
     * @param switchId the node id
     * @return the switch node
     */
    public SwitchNode getSwitchNode( final String switchId )
    {
        // check that nodeId is not null or blank (" ")
        if ( StringUtils.isBlank( switchId ) )
        {
            logger.warn( "In getSwitchNode, nodeId is either null or blank." );
            return null;
        }
        // get all switches of the inventory
        List<SwitchNode> switchNodes = this.getSwitches();
        if ( switchNodes == null || switchNodes.size() == 0 )
        {
            logger.warn( "In getSwitchNode, failed to get switches of inventory file '{}'.", this.inventoryFile );
            return null;
        }
        for ( SwitchNode switchNode : switchNodes )
        {
            if ( switchNode != null && StringUtils.equals( switchNode.getSwitchId(), switchId ) )
            {
                return switchNode;
            }
        }
        return null;
    }

    /**
     * Adds the host to the inventory.
     *
     * @param serverNode the server node
     * @return true, if successful
     */
    public boolean addHost( ServerNode serverNode )
    {

        // check that ServerNode is not null
        if ( serverNode == null )
        {
            logger.warn( "In addHost, ServerNode is null." );
            return false;
        }

        // Get all the existing hosts
        List<ServerNode> hosts = this.getHosts();
        if ( hosts == null )
        {
            logger.warn( "In addHost, invenotry does not contain any hosts." );
            hosts = new ArrayList<ServerNode>();
        }

        // add new host
        hosts.add( serverNode );
        return this.setHosts( hosts );
    }

    /**
     * Removes the Host with hostId from inventory.
     *
     * @param hostId the host id
     * @return true, if successful
     */
    public boolean removeHost( final String hostId )
    {

        // check that hostId is not null or blank (" ")
        if ( StringUtils.isBlank( hostId ) )
        {
            logger.warn( "In removeHost, HostID is either null or blank." );
            return false;
        }

        // Get all the existing hosts
        List<ServerNode> hosts = this.getHosts();
        if ( hosts == null )
        {
            logger.error( "In removeHost, inventory does not contain any hosts. Host '{}' can't be removed.", hostId );
            return false;
        }

        // remove the host from inventory
        boolean serverRemoved = false;
        for ( Iterator<ServerNode> iterator = hosts.iterator(); iterator.hasNext(); )
        {
            ServerNode serverNode = iterator.next();
            if ( StringUtils.equals( serverNode.getNodeID(), hostId ) )
            {
                logger.debug( "In removeHost, Host '{}' found in inventory. Removing it from inventory.", hostId );
                iterator.remove();
                serverRemoved = true;
                break;
            }
        }

        /*
         * If Host is found and removed from Host's list, update inventory node map.
         */
        if ( serverRemoved )
        {
            logger.info( "In removeHost, Host '{}' is removed from inventory.", hostId );
            return this.setHosts( hosts );

        }
        else
        {
            logger.warn( "In removeHost, Host '{}' not found in inventory.", hostId );
            return false;
        }
    }

    /**
     * Sets the hosts.
     *
     * @param hosts the hosts
     * @return true, if successful
     */
    public boolean setHosts( List<ServerNode> hosts )
    {

        // check hosts is not null
        if ( hosts == null )
        {
            logger.warn( "In updateHosts, hosts is null." );
            return false;
        }

        // try updating node map thread safe way
        if ( reentrantLock.tryLock() )
        {
            try
            {
                this.nodeMap.put( HOSTS, hosts.toArray() );
                return true;
            }
            finally
            {
                reentrantLock.unlock();
            }
        }
        else
        {
            logger.warn( "In setHosts, failed to acquire lock for updating node map with hosts. "
                + "Updating node map, non thread safe way." );
            this.nodeMap.put( HOSTS, hosts.toArray() );
            return true;
        }
    }
}
