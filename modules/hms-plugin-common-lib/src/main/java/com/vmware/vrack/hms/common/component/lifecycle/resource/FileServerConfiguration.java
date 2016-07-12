/* ********************************************************************************
 * FileServerConfiguration.java
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
package com.vmware.vrack.hms.common.component.lifecycle.resource;

/**
 * <code>SftpConfiguration</code> class for providing file server configuration details to plugins. <br>
 */
public class FileServerConfiguration
{
    /** The file server type. */
    private FileServerType fileServerType;

    /** The hostname. */
    private String hostname;

    /** The username. */
    private String username;

    /** The password. */
    private String password;

    /** The root directory. */
    private String homeDirectory;

    /**
     * Instantiates a new FileServerConfiguration.
     *
     * @param fileServerType the file server type
     * @param hostname the hostname
     * @param username the username
     * @param password the password
     * @param homeDirectory the home directory
     */
    public FileServerConfiguration( FileServerType fileServerType, String hostname, String username, String password,
                                    String homeDirectory )
    {
        this.fileServerType = fileServerType;
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.homeDirectory = homeDirectory;
    }

    /**
     * Instantiates a new sftp configuration.
     */
    public FileServerConfiguration()
    {
    }

    /**
     * Gets the hostname.
     *
     * @return the hostname
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * Sets the hostname.
     *
     * @param hostname the new hostname
     */
    public void setHostname( String hostname )
    {
        this.hostname = hostname;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the new username
     */
    public void setUsername( String username )
    {
        this.username = username;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword( String password )
    {
        this.password = password;
    }

    /**
     * Gets the file server type.
     *
     * @return the file server type
     */
    public FileServerType getFileServerType()
    {
        return fileServerType;
    }

    /**
     * Sets the file server type.
     *
     * @param fileServerType the new file server type
     */
    public void setFileServerType( FileServerType fileServerType )
    {
        this.fileServerType = fileServerType;
    }

    /**
     * Gets the home directory.
     *
     * @return the home directory
     */
    public String getHomeDirectory()
    {
        return homeDirectory;
    }

    /**
     * Sets the home directory.
     *
     * @param homeDirectory the new home directory
     */
    public void setHomeDirectory( String homeDirectory )
    {
        this.homeDirectory = homeDirectory;
    }
}
