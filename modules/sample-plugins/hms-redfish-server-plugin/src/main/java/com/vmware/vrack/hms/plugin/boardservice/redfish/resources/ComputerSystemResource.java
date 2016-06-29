package com.vmware.vrack.hms.plugin.boardservice.redfish.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.emptyList;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class ComputerSystemResource
    extends RedfishResource
{
    @JsonProperty( "SystemType" )
    private SystemType systemType;

    @JsonProperty( "AssetTag" )
    private String assetTag;

    @JsonProperty( "Manufacturer" )
    private String manufacturer;

    @JsonProperty( "Model" )
    private String model;

    @JsonProperty( "SKU" )
    private String sku;

    @JsonProperty( "SerialNumber" )
    private String serialNumber;

    @JsonProperty( "PartNumber" )
    private String partNumber;

    @JsonProperty( "UUID" )
    private UUID uuid;

    @JsonProperty( "HostName" )
    private String hostName;

    @JsonProperty( "IndicatorLED" )
    private IndicatorLed indicatorLed;

    @JsonProperty( "PowerState" )
    private PowerState powerState;

    @JsonProperty( "Boot" )
    private Boot boot = new Boot();

    @JsonProperty( "BiosVersion" )
    private String biosVersion;

    @JsonProperty( "ProcessorSummary" )
    private ProcessorSummary processorSummary = new ProcessorSummary();

    @JsonProperty( "MemorySummary" )
    private MemorySummary memorySummary = new MemorySummary();

    @JsonProperty( "Actions" )
    private Actions actions = new Actions();

    @JsonProperty( "Status" )
    private Status status;

    /**
     * Navigation properties
     */
    @JsonProperty( "Processors" )
    private OdataId processors;

    @JsonProperty( "EthernetInterfaces" )
    private OdataId ethernetInterfaces;

    @JsonProperty( "SimpleStorage" )
    private OdataId simpleStorages;

    @JsonProperty( "Memory" )
    private OdataId memory;

    @JsonProperty( "LogServices" )
    private OdataId logServices;

    @JsonProperty( "Links" )
    private Links links = new Links();

    @JsonProperty( "Oem" )
    private Object oem = new Object();

    public OdataId getResetActionTarget()
    {
        return actions.reset.target;
    }

    public SystemType getSystemType()
    {
        return systemType;
    }

    public String getAssetTag()
    {
        return assetTag;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public String getModel()
    {
        return model;
    }

    public String getSku()
    {
        return sku;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getPartNumber()
    {
        return partNumber;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public String getHostName()
    {
        return hostName;
    }

    public IndicatorLed getIndicatorLed()
    {
        return indicatorLed;
    }

    public PowerState getPowerState()
    {
        return powerState;
    }

    public Boot getBoot()
    {
        return boot;
    }

    public String getBiosVersion()
    {
        return biosVersion;
    }

    public ProcessorSummary getProcessorSummary()
    {
        return processorSummary;
    }

    public MemorySummary getMemorySummary()
    {
        return memorySummary;
    }

    public Status getStatus()
    {
        return status;
    }

    public Object getOem()
    {
        return oem;
    }

    public OdataId getProcessors()
    {
        return processors;
    }

    public OdataId getEthernetInterfaces()
    {
        return ethernetInterfaces;
    }

    public OdataId getSimpleStorages()
    {
        return simpleStorages;
    }

    public OdataId getMemory()
    {
        return memory;
    }

    public OdataId getLogServices()
    {
        return logServices;
    }

    public List<OdataId> getManagedBy()
    {
        return links.getManagedBy();
    }

    @Override
    public Set<OdataId> getRelatedResources()
    {
        Set<OdataId> relatedResources = new HashSet<>();
        if ( processors != null )
        {
            relatedResources.add( processors );
        }
        if ( ethernetInterfaces != null )
        {
            relatedResources.add( ethernetInterfaces );
        }
        if ( simpleStorages != null )
        {
            relatedResources.add( simpleStorages );
        }
        if ( logServices != null )
        {
            relatedResources.add( logServices );
        }

        for ( OdataId odataId : links.getChassis() )
        {
            relatedResources.add( odataId );
        }
        for ( OdataId odataId : links.getManagedBy() )
        {
            relatedResources.add( odataId );
        }
        for ( OdataId odataId : links.getCooledBy() )
        {
            relatedResources.add( odataId );
        }
        for ( OdataId odataId : links.getPoweredBy() )
        {
            relatedResources.add( odataId );
        }
        return relatedResources;
    }

    public enum SystemType
    {
        Physical,
        Virtual,
        OS,
        PhysicallyPartitioned,
        VirtuallyPartitioned
    }

    public enum IndicatorLed
    {
        Unknown,
        Lit,
        Blinking,
        Off
    }

    public enum PowerState
    {
        On,
        Off,
        PoweringOn,
        PoweringOff
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    private static final class ProcessorSummary
    {
        @JsonProperty( "Count" )
        private Integer count;

        @JsonProperty( "Model" )
        private String model;

        @JsonProperty( "Status" )
        private Status status;
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    private static final class MemorySummary
    {
        @JsonProperty( "TotalSystemMemoryGiB" )
        private Integer totalSystemMemoryGiB;

        @JsonProperty( "Status" )
        private Status status;
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    public static final class Actions
    {
        @JsonProperty( "#ComputerSystem.Reset" )
        private Reset reset = new Reset();

        @JsonProperty( "Oem" )
        private Object oem = new Object();

        public enum ResetType
        {
            On,
            ForceOff,
            GracefulShutdown,
            GracefulRestart,
            ForceRestart,
            Nmi,
            ForceOn,
            PushPowerButton
        }

        @JsonIgnoreProperties( ignoreUnknown = true )
        private static final class Reset
        {
            @JsonProperty( "target" )
            private OdataId target;

            @JsonProperty( "ResetType@Redfish.AllowableValues" )
            private List<ResetType> allowableValues;
        }
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    private static final class Links
    {
        @JsonProperty( "Chassis" )
        private List<OdataId> chassis;

        @JsonProperty( "ManagedBy" )
        private List<OdataId> managedBy;

        @JsonProperty( "PoweredBy" )
        private List<OdataId> poweredBy;

        @JsonProperty( "CooledBy" )
        private List<OdataId> cooledBy;

        @JsonProperty( "Oem" )
        private Object oem = new Object();

        public List<OdataId> getChassis()
        {
            if ( chassis == null )
            {
                return emptyList();
            }
            return chassis;
        }

        public List<OdataId> getManagedBy()
        {
            if ( managedBy == null )
            {
                return emptyList();
            }
            return managedBy;
        }

        public List<OdataId> getPoweredBy()
        {
            if ( poweredBy == null )
            {
                return emptyList();
            }
            return poweredBy;
        }

        public List<OdataId> getCooledBy()
        {
            if ( cooledBy == null )
            {
                return emptyList();
            }
            return cooledBy;
        }
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    public static final class Boot
    {
        @JsonProperty( "BootSourceOverrideEnabled" )
        private BootSourceState bootSourceOverrideEnabled;

        @JsonProperty( "BootSourceOverrideTarget" )
        private BootSourceType bootSourceOverrideTarget;

        public BootSourceState getBootSourceOverrideEnabled()
        {
            return bootSourceOverrideEnabled;
        }

        public void setBootSourceOverrideEnabled(
            BootSourceState bootSourceOverrideEnabled )
        {
            this.bootSourceOverrideEnabled = bootSourceOverrideEnabled;
        }

        public BootSourceType getBootSourceOverrideTarget()
        {
            return bootSourceOverrideTarget;
        }

        public void setBootSourceOverrideTarget(
            BootSourceType bootSourceOverrideTarget )
        {
            this.bootSourceOverrideTarget = bootSourceOverrideTarget;
        }

        @Override
        public String toString()
        {
            final StringBuilder sb = new StringBuilder( "Boot{" );
            sb.append( "bootSourceOverrideEnabled=" ).append( bootSourceOverrideEnabled );
            sb.append( ", bootSourceOverrideTarget=" ).append( bootSourceOverrideTarget );
            sb.append( '}' );
            return sb.toString();
        }

        public enum BootSourceState
        {
            Disabled,
            Once,
            Continuous
        }

        public enum BootSourceType
        {
            None,
            Pxe,
            Floppy,
            Cd,
            Usb,
            Hdd,
            BiosSetup,
            Utilities,
            Diags,
            UefiTarget
        }
    }
}
