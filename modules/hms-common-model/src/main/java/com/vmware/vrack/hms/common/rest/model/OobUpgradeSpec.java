/* ********************************************************************************
 * OobUpgradeSpec.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <code>UpgradeSpec</code> is the model that HMS Aggregator uses while invoking Upgrade service of OOB Agent to upgrade
 * OOB Agent.<br>
 * <p>
 * Spec will have all the required parameters that OOB agent needs to upgrade itself.
 *
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class OobUpgradeSpec
{
    /** The scripts location. */
    private String scriptsLocation;

    /** The HMS token. */
    private String id;

    /** Upgrade binary location. */
    private String location;

    /** Upgrade binary file name. */
    private String fileName;

    /** Upgrade binary checksum value. */
    private String checksum;

    /**
     * Gets the HMS token.
     *
     * @return the upgrade id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the upgrade.
     *
     * @param id Unique id to track the upgrade
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * Gets the location.
     *
     * @return the location
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Sets the location.
     *
     * @param location the new location
     */
    public void setLocation( String location )
    {
        this.location = location;
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Sets the file name.
     *
     * @param fileName the new file name
     */
    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }

    /**
     * Gets the checksum.
     *
     * @return the checksum.
     */
    public String getChecksum()
    {
        return checksum;
    }

    /**
     * Sets the checksum.
     *
     * @param checksum the new checksum
     */
    public void setChecksum( String checksum )
    {
        this.checksum = checksum;
    }

    /**
     * Gets the scripts location.
     *
     * @return the scriptsLocation
     */
    public String getScriptsLocation()
    {
        return scriptsLocation;
    }

    /**
     * Sets the scripts location.
     *
     * @param scriptsLocation the scriptsLocation to set
     */
    public void setScriptsLocation( String scriptsLocation )
    {
        this.scriptsLocation = scriptsLocation;
    }

    /**
     * To string.
     *
     * @return the string
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "[ " );
        sb.append( "id = " + this.id + ", " );
        sb.append( "fileName = " + this.fileName + ", " );
        sb.append( "checksum = " + this.checksum + ", " );
        sb.append( "location = " + this.location + ", " );
        sb.append( "scriptsLocation = " + this.scriptsLocation );
        sb.append( " ]" );
        return sb.toString();
    }
}
