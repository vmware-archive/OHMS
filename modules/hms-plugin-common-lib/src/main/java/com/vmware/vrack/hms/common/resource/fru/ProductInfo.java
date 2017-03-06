/* ********************************************************************************
 * ProductInfo.java
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
package com.vmware.vrack.hms.common.resource.fru;

/**
 * FRU record containing Board info.<br>
 * This area is present if the FRU itself is a separate product. This is typically seen when the FRU is an add-in card.
 * When this area is provided in the FRU Information Device that contains the Chassis Info Area, the product info is for
 * the overall system, as initially manufactured.
 */
public class ProductInfo
    extends FruRecord
{

    private String manufacturerName;

    private String productName;

    private String productModelNumber;

    private String productVersion;

    private String productSerialNumber;

    private String assetTag;

    private byte[] fruFileId = new byte[0];

    private String[] customProductInfo = new String[0];

    public String getManufacturerName()
    {
        return manufacturerName;
    }

    public void setManufacturerName( String manufacturerName )
    {
        this.manufacturerName = manufacturerName;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName( String productName )
    {
        this.productName = productName;
    }

    public String getProductModelNumber()
    {
        return productModelNumber;
    }

    public void setProductModelNumber( String productModelNumber )
    {
        this.productModelNumber = productModelNumber;
    }

    public String getProductVersion()
    {
        return productVersion;
    }

    public void setProductVersion( String productVersion )
    {
        this.productVersion = productVersion;
    }

    public void setProductSerialNumber( String productSerialNumber )
    {
        this.productSerialNumber = productSerialNumber;
    }

    public String getProductSerialNumber()
    {
        return productSerialNumber;
    }

    public String getAssetTag()
    {
        return assetTag;
    }

    public void setAssetTag( String assetTag )
    {
        this.assetTag = assetTag;
    }

    public byte[] getFruFileId()
    {
        return fruFileId;
    }

    public void setFruFileId( byte[] fruFileId )
    {
        this.fruFileId = fruFileId;
    }

    public String[] getCustomProductInfo()
    {
        return customProductInfo;
    }

    public void setCustomProductInfo( String[] customProductInfo )
    {
        this.customProductInfo = customProductInfo;
    }

}
