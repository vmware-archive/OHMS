/* ********************************************************************************
 * SecondBootConfiguration.java
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
package com.vmware.vrack.hms.vsphere.guest;

/**
 * Created by Jeffrey Wang on 9/17/14.
 */
public class SecondBootConfiguration
{
    public String id;

    public int allocatedIpNumber;

    public String vrmTargetEth;

    public String ovfToolLocation;

    public String vsanServiceId;

    public String hostUsername;

    public String hostPassword;

    public PscParameter pscParameter;

    public RenasarParameter renasarParameter;

    public VcenterParameter vcenterParameter;

    public NetworkParameter vxlanNetwork;

    public NetworkParameter mgmtNetwork;

    public NetworkParameter vmotionNetwork;

    public NetworkParameter vsanNetwork;

    public NetworkParameter externalNetwork;

    public NetworkParameter nonRoutableNetwork;

    public NsxParameter nsxParameter;

    public LoginsightParameter loginsightParameter;

    public VcopsParameter vcopsParameter;

    public SecondBootConfiguration()
    {
    }

    public static class PscParameter
    {
        public GuestVmCredential guestVmCredential;

        public String pscDomain;

        public String pscHostname;

        public PscParameter()
        {
        }
    }

    public static class RenasarParameter
    {
        public GuestVmCredential guestVmCredential;

        public RenasarParameter()
        {
        }
    }

    public static class VcenterParameter
    {
        public GuestVmCredential guestVmCredential;

        public String vcIp;

        public String vcTargetEth;

        public String vcHostUsername;

        public String vcHostPassword;

        public String lagName;

        public VcenterParameter()
        {
        }
    }

    public static class NetworkParameter
    {
        public String type;

        public String name;

        public String subnet;

        public String mask;

        public String gateway;

        public Long vlandId = Long.valueOf( 0L );

        public String dns;

        public String ntp;

        public NetworkParameter()
        {
        }
    }

    public static class NsxParameter
    {
        public Boolean isRoutable;

        public Boolean deployNsx;

        public Boolean configureNsx;

        public String serviceId;

        public String vsmUsername;

        public String vsmPassword;

        public String datastoreName;

        public String datacenterName;

        public String clusterName;

        public String vmName;

        public String network;

        public String ovfUrl;

        public String vsmCliAdminPassword;

        public String vsmCliPrivatePassword;

        public String vsmHostname;

        public String vsmNtp;

        public boolean isSshEnabled;

        public String ctrlPoolName;

        public String ctrlPoolCount;

        public String ctrlNumNodes;

        public String ctrlFormFactor;

        public String ctrlPassword;

        public String ctrlCluster;

        public String ctrlDatastore;

        public String ctrlNetwork;

        public String vxlanPoolName;

        public String vxlanPoolCount;

        public String vxlanCluster;

        public String vxlanDVS;

        public String vxlanVlanId;

        public String vxlanMTU;

        public String segmentPoolId;

        public String segmentPoolFirst;

        public String segmentPoolLast;

        public String transportZoneName;

        public String virtualWireName;

        public String tenantName;

        public String edgeNumNodes;

        public String edgeFormFactor;

        public String edgeCluster;

        public String edgeDatastore;

        public String edgePortGroup;

        public NsxParameter()
        {
        }
    }

    public static class LoginsightParameter
    {
        public String deploy;

        public String vmsize;

        public String password;

        public String adminpassword;

        public String networkEth0;

        public String networkEth1;

        public String datastore;

        public String folder;

        public String license;

        public String contentpack;

        public String ovf;
    }

    public static class VcopsParameter
    {
        public String deploy;

        public String vmsize;

        public String password;

        public String adminpassword;

        public String networkEth0;

        public String networkEth1;

        public String datastore;

        public String folder;

        public String license;

        public String ovf;
    }

    public static class GuestVmCredential
    {
        public String guestVmName;

        public String guestUsername;

        public String guestPassword;

        public GuestVmCredential()
        {
        }
    }
}
