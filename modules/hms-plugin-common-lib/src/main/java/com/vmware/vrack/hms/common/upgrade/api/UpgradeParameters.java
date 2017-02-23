/* ********************************************************************************
 * UpgradeParameters.java
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

package com.vmware.vrack.hms.common.upgrade.api;

/**
 * Class for providing upgrade related information, such as Upgrade location, binary names, checksum filename etc.
 * 
 * @author VMware Inc.
 */
public class UpgradeParameters
{
    /**
     * Absolute Path to IB upgrade Binary's Parent directory
     */
    private String ibBinaryParentDir;

    /**
     * Absolute Path to OOB upgrade Binary's Parent directory
     */
    private String oobBinaryParentDir;

    /**
     * Name of the IB binary e.g. "hms-aggregator.war"
     */
    private String ibBinaryName;

    /**
     * Name of the OOB binary e.g. "hms-0.0.1-SNAPSHOT.tar.gz"
     */
    private String oobBinaryName;

    /**
     * Checksum calculation and validation method to be used for IB Binary. Defaults to MD5
     */
    private ChecksumMethod ibChecksumMethod = ChecksumMethod.MD5;

    /**
     * Checksum calculation and validation method to be used for OOB Binary. Defaults to MD5
     */
    private ChecksumMethod oobChecksumMethod = ChecksumMethod.MD5;

    public String getIbBinaryParentDir()
    {
        return ibBinaryParentDir;
    }

    public void setIbBinaryParentDir( String ibBinaryParentDir )
    {
        this.ibBinaryParentDir = ibBinaryParentDir;
    }

    public String getOobBinaryParentDir()
    {
        return oobBinaryParentDir;
    }

    public void setOobBinaryParentDir( String oobBinaryParentDir )
    {
        this.oobBinaryParentDir = oobBinaryParentDir;
    }

    public String getIbBinaryName()
    {
        return ibBinaryName;
    }

    public void setIbBinaryName( String ibBinaryName )
    {
        this.ibBinaryName = ibBinaryName;
    }

    public String getOobBinaryName()
    {
        return oobBinaryName;
    }

    public void setOobBinaryName( String oobBinaryName )
    {
        this.oobBinaryName = oobBinaryName;
    }

    public ChecksumMethod getIbChecksumMethod()
    {
        return ibChecksumMethod;
    }

    public void setIbChecksumMethod( ChecksumMethod ibChecksumMethod )
    {
        this.ibChecksumMethod = ibChecksumMethod;
    }

    public ChecksumMethod getOobChecksumMethod()
    {
        return oobChecksumMethod;
    }

    public void setOobChecksumMethod( ChecksumMethod oobChecksumMethod )
    {
        this.oobChecksumMethod = oobChecksumMethod;
    }

}
