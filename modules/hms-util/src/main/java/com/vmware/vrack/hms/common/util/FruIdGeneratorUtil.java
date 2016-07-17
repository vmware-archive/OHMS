/* ********************************************************************************
 * FruIdGeneratorUtil.java
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
package com.vmware.vrack.hms.common.util;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;

/**
 * Helps to generate the FruID hashcode
 * 
 * @author VMware. Inc
 */
public class FruIdGeneratorUtil
{
    private static Logger logger = Logger.getLogger( FruIdGeneratorUtil.class );

    /**
     * Method to generate the unique FRU ID Hash code
     * 
     * @param componentIdentifier
     * @param location
     * @return hash (hashcode)
     */
    public long generateFruIdHashCode( ComponentIdentifier componentIdentifier, String location )
    {
        long hash = 0;
        try
        {
            if ( componentIdentifier != null )
            {
                if ( componentIdentifier.getManufacturer() != null )
                {
                    hash = componentIdentifier.getManufacturer().hashCode();
                }
                if ( componentIdentifier.getProduct() != null )
                {
                    hash = hash + componentIdentifier.getProduct().hashCode();
                }
                if ( componentIdentifier.getPartNumber() != null )
                {
                    hash = hash + componentIdentifier.getPartNumber().hashCode();
                }
                if ( componentIdentifier.getSerialNumber() != null )
                {
                    hash = hash + componentIdentifier.getSerialNumber().hashCode();
                }
            }
            if ( location != null )
            {
                hash = hash + location.hashCode();
            }
            return hash;
        }
        catch ( Exception e )
        {
            logger.error( " Error while generating the FRU ID " + componentIdentifier + location, e );
        }
        return 0;
    }

    /**
     * Generate the unique FRU ID Hash code for Storage/HDD FRU component
     *
     * @param storageFruId
     * @param serverFruId
     * @param diskType
     * @return String (HashCode)
     */
    public String generateFruIdHashCodeStorage( Long storageFruId, String serverFruId, String diskType )
    {
        long hash = 0;
        String hashCode = null;
        try
        {
            if ( storageFruId != null )
            {
                hash = storageFruId;
                if ( diskType != null )
                {
                    hash = hash + diskType.hashCode();
                }
            }
            if ( serverFruId != null )
            {
                hashCode = String.valueOf( hash ) + serverFruId;
            }
            return hashCode;
        }
        catch ( Exception e )
        {
            logger.error( " Error while generating the FRU ID for Storage or HDD FRU component" + storageFruId
                + serverFruId, e );
        }
        return hashCode;
    }

    /**
     * Generate the unique FRU ID Hash code for Ethernet Controller FRU component
     *
     * @param EthernetControllerFruId
     * @param macAddress
     * @param serverFruId
     * @return String (HashCode)
     */
    public String generateFruIdHashCodeEthernetController( Long EthernetControllerFruId, String macAddress,
                                                           String serverFruId )
    {
        long hash = 0;
        String hashCode = null;
        try
        {
            if ( EthernetControllerFruId != null )
            {
                hash = EthernetControllerFruId;
                if ( macAddress != null )
                {
                    hash = hash + macAddress.hashCode();
                }
            }
            if ( serverFruId != null )
            {
                hashCode = String.valueOf( hash ) + serverFruId;
            }
            return hashCode;
        }
        catch ( Exception e )
        {
            logger.error( " Error while generating the FRU ID for Ethernet Controller FRU component" + serverFruId
                + macAddress, e );
        }
        return hashCode;
    }

    /**
     * Generate the unique FRU ID Hash code for Storage Controller FRU component
     *
     * @param storageControllerFruId
     * @param hbaDeviceName
     * @param serverFruId
     * @return String (HashCode)
     */
    public String generateFruIdHashCodeStorageController( Long storageControllerFruId, String hbaDeviceName,
                                                          String serverFruId )
    {
        long hash = 0;
        String hashCode = null;
        try
        {
            if ( storageControllerFruId != null )
            {
                hash = storageControllerFruId;
                if ( hbaDeviceName != null )
                {
                    hash = hash + hbaDeviceName.hashCode();
                }
            }
            if ( serverFruId != null )
            {
                hashCode = String.valueOf( hash ) + serverFruId;
            }
            return hashCode;
        }
        catch ( Exception e )
        {
            logger.error( " Error while generating the FRU ID for Storage Controller FRU component" + serverFruId
                + hbaDeviceName, e );
        }
        return hashCode;
    }
}
