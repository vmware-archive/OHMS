/* ********************************************************************************
 * ChecksumMethod.java
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
package com.vmware.vrack.hms.common.upgrade.api;

/**
 * Specifies which checksum calculation and validation method will be used for upgrade binary integrity check.
 *
 * @author VMware Inc.
 */
public enum ChecksumMethod
{
    /*
     * Valid checksum algorithms are Ref: http://docs.oracle.com/javase/6/docs/technotes/guides/security/SunProviders
     * .html MD2 MD5 SHA-1 SHA-256 SHA-384 SHA-512
     */
    /** The md5. */
    MD5( "MD5" ), /** The sha1. */
    SHA1( "SHA-1" );
    private String checksumMethod;

    private ChecksumMethod( String checkSumMethod )
    {
        this.checksumMethod = checkSumMethod;
    }

    /**
     * Checks if the given String is a valid ChecksumMethod or not.
     *
     * @param value String to check if it is a valid ChecksumMethod.
     * @return Returns true, if the given String is a valid ChecksumMethod. Otherwise, returns false.
     */
    public static boolean contains( String value )
    {
        for ( ChecksumMethod c : ChecksumMethod.values() )
        {
            if ( c.toString().equals( value ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @return ChecksumMethod as String
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()
    {
        return this.checksumMethod;
    }
}
