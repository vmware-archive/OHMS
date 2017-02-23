/* ********************************************************************************
 * Svi.java
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
package com.vmware.vrack.hms.switches.cumulus.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Svi
    extends ConfigBlock
{

    public static String format = "auto %s.%s\n" + "iface %s.%s";

    private static String addressLiteral = "address";

    private static String querierIpLiteral = "bridge-igmp-querier-src";

    static Pattern sviPattern =
        Pattern.compile( String.format( "\\s*(%s|%s) (.*)$", addressLiteral, querierIpLiteral ) );

    /*
     * We only have what we need now and nothing else, hence we dont have ports here which is managed purely using VLAN
     * aware bridge configuration as of now
     */
    private String address;

    private int prefixLen;

    private String igmpQuerierAddress;

    /**
     * @return the address
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress( String address )
    {
        this.address = address;
    }

    /**
     * @return the prefixLen
     */
    public int getPrefixLen()
    {
        return prefixLen;
    }

    /**
     * @param prefixLen the prefixLen to set
     */
    public void setPrefixLen( int prefixLen )
    {
        this.prefixLen = prefixLen;
    }

    /**
     * @return the igmpQuerierAddress
     */
    public String getIgmpQuerierAddress()
    {
        return igmpQuerierAddress;
    }

    /**
     * @param igmpQuerierAddress the igmpQuerierAddress to set
     */
    public void setIgmpQuerierAddress( String igmpQuerierAddress )
    {
        this.igmpQuerierAddress = igmpQuerierAddress;
    }

    /**
     * Get the string of Switch Vlan Interface details.
     *
     * @return string with details for the bridge
     */
    public String getString()
    {
        /* name is coming from the superclass */
        String retString = "";
        if ( address == null )
            return retString;

        retString = String.format( format, name, pvid, name, pvid );
        if ( address != "" && prefixLen != 0 )
            retString += "\n    " + addressLiteral + " " + getAddress() + "/" + getPrefixLen();
        if ( igmpQuerierAddress != null && igmpQuerierAddress != "" )
            retString += "\n    " + querierIpLiteral + " " + getIgmpQuerierAddress();

        if ( otherConfig != "" )
            retString += "\n" + otherConfig;

        return retString;
    }

    /**
     * Add another configuration detail for the SVI
     *
     * @param aLine
     * @param configuration details
     * @return void
     */
    public void addOtherConfig( String aLine, Configuration configuration )
    {
        Matcher matcher = sviPattern.matcher( aLine );
        if ( matcher.matches() )
        {
            /* That is a go ahead for us to parse rest of it */
            if ( matcher.group( 1 ).equals( addressLiteral ) )
            {
                String tokens[] = matcher.group( 2 ).split( "/" );

                if ( tokens.length < 2 )
                    return; /* Notify parsing error */

                setAddress( tokens[0] );
                setPrefixLen( Integer.parseInt( tokens[1] ) );
            }
            else if ( matcher.group( 1 ).equals( querierIpLiteral ) )
            {
                setIgmpQuerierAddress( matcher.group( 2 ) );
            }
        }
        else
        {
            super.addOtherConfig( aLine, configuration );
        }
    }
}
