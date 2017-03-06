/* ********************************************************************************
 * SwitchSnmpUser.java
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
package com.vmware.vrack.hms.common.switches.api;

public class SwitchSnmpUser
{
    public enum AuthType
    {
        SHA, MD5
    }

    public enum PrivType
    {
        AES, DES
    }

    /* (MANDATORY) Self explanatory. This is the user identifier */
    private String username;

    /* (MANDATORY) type of authentication when communicating with the manager, supported types are SHA and MD5 */
    private AuthType authType;

    /* (MANDATORY) password to be used during authentication procedure */
    private String authPassword;

    /*
     * (OPTIONAL) we may choose to encrypt all communication with the SNMP Manager and if yes then this should be set to
     * types AES or DES.
     */
    private PrivType privType;

    /*
     * (MUST IF privType is NOT NULL) if encryption of all communication is desired by setting privType to AES/DES then
     * this must be set and would act as the encryption algorithm seed
     */
    private String privPassword;

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public AuthType getAuthType()
    {
        return authType;
    }

    public void setAuthType( AuthType authType )
    {
        this.authType = authType;
    }

    public String getAuthPassword()
    {
        return authPassword;
    }

    public void setAuthPassword( String authPassword )
    {
        this.authPassword = authPassword;
    }

    public PrivType getPrivType()
    {
        return privType;
    }

    public void setPrivType( PrivType privType )
    {
        this.privType = privType;
    }

    public String getPrivPassword()
    {
        return privPassword;
    }

    public void setPrivPassword( String privPassword )
    {
        this.privPassword = privPassword;
    }
}
