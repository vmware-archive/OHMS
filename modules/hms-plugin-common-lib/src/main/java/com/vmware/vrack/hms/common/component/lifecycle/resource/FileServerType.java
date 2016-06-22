/* ********************************************************************************
 * FileServerType.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.component.lifecycle.resource;

/**
 * <code>FileServerType</code><br>
 * Defines file server types.
 */
public enum FileServerType
{
    /** The tftp. */
    TFTP( "tftp" ), /** The sftp. */
    SFTP( "sftp" ), /** The ftp. */
    FTP( "ftp" );
    /** The file server type. */
    private String fileServerType;

    /**
     * Instantiates a new file server type.
     *
     * @param fileServerType the file server type
     */
    private FileServerType( String fileServerType )
    {
        this.fileServerType = fileServerType;
    }

    /**
     * Returns the file server type.
     *
     * @return the file server type
     */
    public String getFileServerType()
    {
        return fileServerType;
    }
}
