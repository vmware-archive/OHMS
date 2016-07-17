/* ********************************************************************************
 * ComponentIdentifier.java
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
package com.vmware.vrack.hms.common.servernodes.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author VMware, Inc. The purpose of this bean to have FRU component Identifiers which helps to indentify the FRU
 *         Every FRU to have the component identifiers
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ComponentIdentifier
{
    /**
     * @String description - is the description of the FRU
     */
    private String description;

    /**
     * @String manufacturer - The FRU component manufacturer/vendor
     */
    private String manufacturer;

    /**
     * @String product - The FRU component product name/model
     */
    private String product;

    /**
     * @String partNumber - The FRU component part number
     */
    private String partNumber;

    /**
     * @String manufacturingDate - The FRU component manufacturing date details
     */
    private String manufacturingDate;

    /**
     * @String serialNumber - The FRU component serial number
     */
    private String serialNumber;

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer( String manufacturer )
    {
        this.manufacturer = manufacturer;
    }

    public String getProduct()
    {
        return product;
    }

    public void setProduct( String product )
    {
        this.product = product;
    }

    public String getPartNumber()
    {
        return partNumber;
    }

    public void setPartNumber( String partNumber )
    {
        this.partNumber = partNumber;
    }

    public String getManufacturingDate()
    {
        return manufacturingDate;
    }

    public void setManufacturingDate( String manufacturingDate )
    {
        this.manufacturingDate = manufacturingDate;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber( String serialNumber )
    {
        this.serialNumber = serialNumber;
    }
}
