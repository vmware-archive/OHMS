/* ********************************************************************************
 * SwitchType.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.api;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SwitchType
{
    private String manufacturer;

    private String model;

    private boolean regexMatching = true;

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer( String manufacturer )
    {
        this.manufacturer = manufacturer;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel( String model )
    {
        this.model = model;
    }

    @JsonIgnore
    public boolean isRegexMatching()
    {
        return regexMatching;
    }

    @JsonIgnore
    public void setRegexMatching( boolean regexMatching )
    {
        this.regexMatching = regexMatching;
    }

    public boolean matches( SwitchType other )
    {
        if ( !isRegexMatching() && manufacturer != null && other.manufacturer != null && model != null
            && other.model != null && manufacturer.equalsIgnoreCase( other.manufacturer )
            && model.equalsIgnoreCase( other.model ) )
            return true;
        if ( isRegexMatching() && manufacturer != null && other.manufacturer != null && model != null
            && other.model != null
            && Pattern.compile( manufacturer, Pattern.CASE_INSENSITIVE ).matcher( other.manufacturer ).matches()
            && Pattern.compile( model, Pattern.CASE_INSENSITIVE ).matcher( other.model ).matches() )
            return true;
        return false;
    }
}
