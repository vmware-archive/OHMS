/*
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.vmware.vrack.hms.plugin.boardservice.redfish.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Set;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class ProcessorResource
    extends RedfishResource
{
    @JsonProperty( "Socket" )
    private String socket;

    @JsonProperty( "ProcessorType" )
    private ProcessorType processorType;

    @JsonProperty( "ProcessorArchitecture" )
    private String processorArchitecture;

    @JsonProperty( "InstructionSet" )
    private String instructionSet;

    @JsonProperty( "Manufacturer" )
    private String manufacturer;

    @JsonProperty( "Model" )
    private String model;

    @JsonProperty( "MaxSpeedMHz" )
    private Integer maxSpeedMhz;

    @JsonProperty( "TotalCores" )
    private Integer totalCores;

    @JsonProperty( "TotalThreads" )
    private Integer totalThreads;

    @JsonProperty( "Status" )
    private Status status;

    @JsonProperty( "ProcessorId" )
    private ProcessorId processorId;

    @JsonProperty( "Oem" )
    private Object oem = new Object();

    public String getSocket()
    {
        return socket;
    }

    public ProcessorType getProcessorType()
    {
        return processorType;
    }

    public String getProcessorArchitecture()
    {
        return processorArchitecture;
    }

    public String getInstructionSet()
    {
        return instructionSet;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public String getModel()
    {
        return model;
    }

    public Integer getMaxSpeedMhz()
    {
        return maxSpeedMhz;
    }

    public Integer getTotalCores()
    {
        return totalCores;
    }

    public Integer getTotalThreads()
    {
        return totalThreads;
    }

    public Status getStatus()
    {
        return status;
    }

    public ProcessorId getProcessorId()
    {
        return processorId;
    }

    public Object getOem()
    {
        return oem;
    }

    @Override
    public Set<OdataId> getRelatedResources()
    {
        return Collections.emptySet();
    }

    public enum ProcessorType
    {
        CPU,
        GPU,
        FPGA,
        DSP,
        Accelerator,
        OEM
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    public static class ProcessorId
    {
        @JsonProperty( "VendorId" )
        private String vendorId;

        @JsonProperty( "IdentificationRegisters" )
        private String identificationRegisters;

        @JsonProperty( "EffectiveFamily" )
        private String effectiveFamily;

        @JsonProperty( "EffectiveModel" )
        private String effectiveModel;

        @JsonProperty( "Step" )
        private String step;

        @JsonProperty( "MicrocodeInfo" )
        private String microcodeInfo;

        public String getVendorId()
        {
            return vendorId;
        }

        public String getIdentificationRegisters()
        {
            return identificationRegisters;
        }

        public String getEffectiveFamily()
        {
            return effectiveFamily;
        }

        public String getEffectiveModel()
        {
            return effectiveModel;
        }

        public String getStep()
        {
            return step;
        }

        public String getMicrocodeInfo()
        {
            return microcodeInfo;
        }
    }
}
