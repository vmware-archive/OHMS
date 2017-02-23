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

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;

/**
 * Helps to generate the FruID hash code
 * 
 * @author VMware. Inc
 */
public class FruIdGeneratorUtil
{
    private static Logger logger = LoggerFactory.getLogger( FruIdGeneratorUtil.class );

    /**
     * Method to generate the unique FRU ID Hash code with FRU componentIdentifier and location
     * 
     * @param componentIdentifier
     * @param location
     * @return hashCode
     */
    public static String generateFruIdHashCode( ComponentIdentifier componentIdentifier, String location )
    {
        String hash = "0";

        try
        {
            if ( componentIdentifier != null )
            {
                if ( componentIdentifier.getManufacturer() != null )
                {
                    hash = String.valueOf( componentIdentifier.getManufacturer().hashCode() );
                }
                if ( componentIdentifier.getProduct() != null )
                {
                    hash = hash.concat( String.valueOf( componentIdentifier.getProduct().hashCode() ) );
                }
                if ( componentIdentifier.getPartNumber() != null )
                {
                    hash = hash.concat( String.valueOf( componentIdentifier.getPartNumber().hashCode() ) );
                }
                if ( componentIdentifier.getSerialNumber() != null )
                {
                    hash = hash.concat( String.valueOf( componentIdentifier.getSerialNumber().hashCode() ) );
                }
            }
            if ( location != null )
            {
                hash = hash.concat( String.valueOf( location.hashCode() ) );
            }
            return String.valueOf( hash.hashCode() );
        }
        catch ( Exception e )
        {
            logger.error( " Error while generating the FRU ID {} ", componentIdentifier, location, e );
        }
        return hash;
    }

    /**
     * Method to generate the unique FRU ID Hash code with fruId of the Server Component and server FRU ID
     * 
     * @param fruId
     * @param serverFruId
     * @return String (HashCode)
     */
    public static String generateFruIdHashCodeServerComponent( String fruId, String serverFruId )
    {
        String hash = "0";

        try
        {
            if ( fruId != null )
            {
                hash = fruId;
            }

            if ( serverFruId != null )
            {
                hash = hash.concat( serverFruId );
            }
            return String.valueOf( hash.hashCode() );
        }
        catch ( Exception e )
        {
            logger.error( " Error while generating the FRU ID for Server FRU component {}", fruId, serverFruId, e );
        }
        return hash;
    }

    /**
     * Method to generate the unique FRU ID Hash code with fruId of the Server Component and server FRU ID and with
     * uniqueFruProperty
     * 
     * @param fruId
     * @param serverFruId
     * @param uniqueFruProperty
     * @return String (HashCode)
     */
    public static String generateFruIdHashCodeServerComponent( String fruId, String serverFruId,
                                                               String uniqueFruProperty )
    {
        String hash = "0";

        try
        {
            if ( fruId != null )
            {
                hash = fruId;
                if ( uniqueFruProperty != null )
                {
                    hash = hash.concat( String.valueOf( uniqueFruProperty.hashCode() ) );
                }
            }

            if ( serverFruId != null )
            {
                hash = hash.concat( serverFruId );
            }

            return String.valueOf( hash.hashCode() );
        }
        catch ( Exception e )
        {
            logger.error( " Error while generating the FRU ID for Server FRU component {}", fruId, serverFruId, e );
        }
        return hash;
    }

}