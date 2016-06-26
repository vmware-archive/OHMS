package com.vmware.vrack.hms.plugin.boardservice.redfish.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.emptyList;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class ManagerResource
    extends RedfishResource
{
    @JsonProperty( "UUID" )
    private UUID uuid;

    @JsonProperty( "ManagerType" )
    private ManagerType managerType;

    @JsonProperty( "ServiceEntryPointUUID" )
    private UUID serviceEntryPointUuid;

    @JsonProperty( "Model" )
    private String model;

    @JsonProperty( "Status" )
    private Status status;

    @JsonProperty( "GraphicalConsole" )
    private GraphicalConsole graphicalConsole;

    @JsonProperty( "SerialConsole" )
    private Console serialConsole;

    @JsonProperty( "CommandShell" )
    private Console commandShell;

    @JsonProperty( "FirmwareVersion" )
    private String firmwareVersion;

    @JsonProperty( "NetworkProtocol" )
    private OdataId networkProtocol;

    @JsonProperty( "EthernetInterfaces" )
    private OdataId ethernetInterfaces;

    @JsonProperty( "Links" )
    private Links links;

    public UUID getUuid()
    {
        return uuid;
    }

    public ManagerType getManagerType()
    {
        return managerType;
    }

    public UUID getServiceEntryPointUuid()
    {
        return serviceEntryPointUuid;
    }

    public String getModel()
    {
        return model;
    }

    public Status getStatus()
    {
        return status;
    }

    public GraphicalConsole getGraphicalConsole()
    {
        return graphicalConsole;
    }

    public Console getSerialConsole()
    {
        return serialConsole;
    }

    public Console getCommandShell()
    {
        return commandShell;
    }

    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }

    public OdataId getNetworkProtocol()
    {
        return networkProtocol;
    }

    public OdataId getEthernetInterfaces()
    {
        return ethernetInterfaces;
    }

    @Override
    public Set<OdataId> getRelatedResources()
    {
        Set<OdataId> relatedResources = new HashSet<>();
        if ( networkProtocol != null )
        {
            relatedResources.add( networkProtocol );
        }
        if ( ethernetInterfaces != null )
        {
            relatedResources.add( ethernetInterfaces );
        }
        for ( OdataId odataId : links.getManagerForChassis() )
        {
            relatedResources.add( odataId );
        }
        for ( OdataId odataId : links.getManagerForServers() )
        {
            relatedResources.add( odataId );
        }
        for ( OdataId odataId : links.getManagerForSwitches() )
        {
            relatedResources.add( odataId );
        }
        if ( links.getManagerInChassis() != null )
        {
            relatedResources.add( links.getManagerInChassis() );
        }
        return relatedResources;
    }

    public enum ManagerType
    {
        ManagementController,
        EnclosureManager,
        BMC,
        RackManager,
        AuxiliaryController
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    public static final class GraphicalConsole
    {
        @JsonProperty( "ServiceEnabled" )
        private Boolean serviceEnabled;

        @JsonProperty( "MaxConcurrentSessions" )
        private Integer maxConcurrentSessions;

        @JsonProperty( "ConnectTypesSupported" )
        private List<ConnectType> connectTypesSupported;

        public Boolean getServiceEnabled()
        {
            return serviceEnabled;
        }

        public Integer getMaxConcurrentSessions()
        {
            return maxConcurrentSessions;
        }

        public List<ConnectType> getConnectTypesSupported()
        {
            if ( connectTypesSupported == null )
            {
                return emptyList();
            }
            return connectTypesSupported;
        }

        public enum ConnectType
        {
            KVMIP,
            Oem
        }
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    public static final class Console
    {
        @JsonProperty( "ServiceEnabled" )
        private Boolean serviceEnabled;

        @JsonProperty( "MaxConcurrentSessions" )
        private Integer maxConcurrentSessions;

        @JsonProperty( "ConnectTypesSupported" )
        private List<ConnectType> connectTypesSupported;

        public Boolean getServiceEnabled()
        {
            return serviceEnabled;
        }

        public Integer getMaxConcurrentSessions()
        {
            return maxConcurrentSessions;
        }

        public List<ConnectType> getConnectTypesSupported()
        {
            if ( connectTypesSupported != null )
            {
                return emptyList();
            }
            return connectTypesSupported;
        }

        public enum ConnectType
        {
            SSH,
            Telnet,
            IPMI,
            Oem
        }
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    private static final class Links
    {
        @JsonProperty( "ManagerForServers" )
        private List<OdataId> managerForServers;

        @JsonProperty( "ManagerForChassis" )
        private List<OdataId> managerForChassis;

        @JsonProperty( "ManagerInChassis" )
        private OdataId managerInChassis;

        @JsonProperty( "ManagerForSwitches" )
        private List<OdataId> managerForSwitches;

        @JsonProperty( "Oem" )
        private Object oem = new Object();

        public List<OdataId> getManagerForServers()
        {
            if ( managerForServers == null )
            {
                return emptyList();
            }
            return managerForServers;
        }

        public List<OdataId> getManagerForChassis()
        {
            if ( managerForChassis == null )
            {
                return emptyList();
            }
            return managerForChassis;
        }

        public OdataId getManagerInChassis()
        {
            return managerInChassis;
        }

        public List<OdataId> getManagerForSwitches()
        {
            if ( managerForSwitches == null )
            {
                return emptyList();
            }
            return managerForSwitches;
        }

        public Object getOem()
        {
            return oem;
        }
    }
}
