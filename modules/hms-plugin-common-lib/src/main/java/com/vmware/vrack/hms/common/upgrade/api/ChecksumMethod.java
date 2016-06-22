/* ********************************************************************************
 * ChecksumMethod.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
