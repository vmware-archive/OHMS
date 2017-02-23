/* ********************************************************************************
 * DhcpLeaseParser.java
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
package com.vmware.vrack.hms.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.common.rest.model.DhcpLease;
import com.vmware.vrack.hms.common.util.FileUtil;

/**
 * The Class DhcpLeaseParser.
 */
public class DhcpLeaseParser
{

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger( DhcpLeaseParser.class );

    /** The Constant DHCP_LEASE_REGEX. */
    private static final String DHCP_LEASE_REGEX_PATTERN = "lease\\s([01]?\\d\\d?|2[0-4]\\d|25[0-5])"
        + "\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\s\\{(.+?)\\}";

    /**
     * The standard (IEEE 802) format for printing MAC-48 addresses in human-friendly form is six groups of two
     * hexadecimal digits, separated by hyphens - or colons :.
     */
    private static final String MAC_ADDRESS_REGEX_PATTERN = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

    /** The Constant IPV4_ADDRESS_REGEX_PATTERN. */
    private static final String IPV4_ADDRESS_REGEX_PATTERN =
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

    /** The Constant DHCP_LEASE_DATE_PATTERN. */
    private static final String DHCP_LEASE_DATE_PATTERN = "^\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}$";

    /** The simple date format. */
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );

    /**
     * Instantiates a new dhcp lease parser.
     */
    private DhcpLeaseParser()
    {
        throw new AssertionError( "DhcpLeaseParser is a utility class with static methods. Instance not required." );
    }

    /**
     * Parses the lease file.
     *
     * @param dhcpLeaseFile the dhcp lease file
     * @return the list
     */
    public static List<DhcpLease> parseLeaseFile( final String dhcpLeaseFile )
    {

        // check that dhcp lease file is not null or blank.
        if ( StringUtils.isBlank( dhcpLeaseFile ) )
        {
            logger.warn( "In parseLeaseFile, DHCP lease file is either null or blank." );
            return null;
        }

        // check that dhcp lease file exists.
        if ( !FileUtil.isFileExists( dhcpLeaseFile ) )
        {
            logger.warn( "In parseLeaseFile, DHCP lease file '{}' does not exist.", dhcpLeaseFile );
            return null;
        }

        // read dhcp file contents
        final String dhcpLeaseFileContent = FileUtil.readFileToString( dhcpLeaseFile );
        if ( StringUtils.isBlank( dhcpLeaseFileContent ) )
        {
            logger.warn( "In parseLeaseFile, DHCP lease file '{}' content is either null or blank." );
            return null;
        }

        Map<String, DhcpLease> dhcpLeasesMap = new HashMap<String, DhcpLease>();
        Pattern pattern = Pattern.compile( DHCP_LEASE_REGEX_PATTERN, Pattern.DOTALL );
        Matcher matcher = pattern.matcher( dhcpLeaseFileContent );
        DhcpLease dhcpLease = null;
        String macAddress = null;
        while ( matcher.find() )
        {
            dhcpLease = parseLeaseContent( matcher.group() );
            if ( dhcpLease != null )
            {
                macAddress = dhcpLease.getMacAddress();
                if ( !dhcpLeasesMap.containsKey( macAddress ) )
                {
                    dhcpLeasesMap.put( macAddress, dhcpLease );
                }
                else
                {
                    if ( isLatestLease( dhcpLeasesMap.get( macAddress ), dhcpLease ) )
                    {
                        dhcpLeasesMap.put( macAddress, dhcpLease );
                    }
                }
            }
        }
        return new ArrayList<DhcpLease>( dhcpLeasesMap.values() );
    }

    /**
     * Parses the lease content.
     *
     * @param leaseContent the lease content
     * @return the dhcp lease
     */
    public static DhcpLease parseLeaseContent( final String leaseContent )
    {

        // check that leaseContent is not null or blank
        if ( StringUtils.isBlank( leaseContent ) )
        {
            logger.warn( "In parseLeaseContent, lease content is either null or blank." );
            return null;
        }

        // split leaseContent after each line break
        String[] lines = leaseContent.split( "\\r|\\n" );

        // first line contains IP address.
        String ipAddress = lines[0].split( "\\s" )[1];
        if ( StringUtils.isBlank( ipAddress ) )
        {
            logger.debug( "In parseLeaseContent, IP Address is null or blank of the lease: {}.", leaseContent );
            return null;
        }

        if ( !Pattern.matches( IPV4_ADDRESS_REGEX_PATTERN, ipAddress ) )
        {
            logger.warn( "In parseLeaseContent, IP Address '{}' is not a valid IPv4 Address.", ipAddress );
            return null;
        }

        // parse the remaining lines for mac address
        String line = null;
        String singleKey = null;
        String dualKey = null;
        String[] tokens = null;
        String macAddress = null;
        String starts = null;
        String ends = null;
        String bindingState = null;
        String clientHostname = null;
        for ( int index = 1; index < lines.length - 1; index++ )
        {

            line = lines[index].trim();

            // skip if line is blank
            if ( StringUtils.isBlank( line ) )
            {
                continue;
            }

            // replace line ending characters with blank string.
            line = line.replaceAll( "\\n", "" );
            line = line.replaceAll( "\\r", "" );
            lines[index] = line.substring( 0, line.indexOf( ';' ) );
            tokens = lines[index].trim().split( "\\s" );
            singleKey = tokens[0];
            dualKey = String.format( "%s %s", tokens[0], tokens[1] );

            if ( StringUtils.equals( singleKey, "starts" ) )
            {
                // starts 0 2016/06/19 10:06:00;
                starts = DhcpLeaseParser.getLeaseDate( tokens );
            }
            else if ( StringUtils.equals( singleKey, "ends" ) )
            {
                // ends 1 2016/06/20 10:06:00;
                ends = DhcpLeaseParser.getLeaseDate( tokens );
            }
            else if ( StringUtils.equals( dualKey, "binding state" ) )
            {
                if ( tokens.length == 3 )
                {
                    bindingState = tokens[2];
                }
            }
            else if ( StringUtils.equals( singleKey, "client-hostname" ) )
            {
                // client-hostname "QCT2C600CFC788A"
                if ( tokens.length == 2 )
                {
                    // strip off quote
                    if ( tokens[1].startsWith( "\"" ) && tokens[1].endsWith( "\"" ) )
                    {
                        clientHostname = tokens[1].substring( 1, tokens[1].length() - 1 );
                    }
                }
            }
            else if ( StringUtils.equals( dualKey, "hardware ethernet" ) )
            {
                // hardware ethernet 00:00:00:00:00:00;
                if ( StringUtils.isNotBlank( tokens[2] ) )
                {
                    if ( Pattern.matches( MAC_ADDRESS_REGEX_PATTERN, tokens[2] ) )
                    {
                        macAddress = tokens[2];
                    }
                }
            }
        }
        if ( StringUtils.isNotBlank( ipAddress ) && StringUtils.isNotBlank( macAddress )
            && StringUtils.isNotBlank( ends ) && StringUtils.isNotBlank( bindingState ) )
        {
            DhcpLease dhcpLease = new DhcpLease();
            dhcpLease.setIpAddress( ipAddress );
            dhcpLease.setMacAddress( macAddress );
            dhcpLease.setEnds( ends );
            dhcpLease.setBindingState( bindingState );
            if ( StringUtils.isNotBlank( starts ) )
            {
                dhcpLease.setStarts( starts );
            }
            if ( StringUtils.isNotBlank( clientHostname ) )
            {
                dhcpLease.setClientHostname( clientHostname );
            }
            logger.debug( "In parseLeaseContent, DHCP Lease: {}", dhcpLease.toString() );
            return dhcpLease;
        }
        return null;
    }

    /**
     * Parses the given tokens array and extracts the date from it.
     *
     * @param tokens the tokens
     * @return the lease date
     */
    private static String getLeaseDate( final String[] tokens )
    {
        String date = null;
        if ( ArrayUtils.isEmpty( tokens ) )
        {
            logger.warn( "In getLeaseDate, tokens array is either null or empty." );
            return null;
        }
        // starts 0 2016/06/19 10:06:00;
        // ends 1 2016/06/20 10:06:00;
        if ( tokens.length == 4 )
        {
            date = String.format( "%s %s", tokens[2], tokens[3] );
            if ( Pattern.matches( DHCP_LEASE_DATE_PATTERN, date ) )
            {
                return date;
            }
        }
        return null;
    }

    /**
     * Checks if is latest lease.
     *
     * @param existingLease the existing lease
     * @param newLease the new lease
     * @return true, if is latest lease
     */
    public static boolean isLatestLease( DhcpLease existingLease, DhcpLease newLease )
    {
        if ( existingLease == null || newLease == null )
        {
            logger.warn( "In isLatestLease, either existing lease is null or new lease is null." );
            return false;
        }
        if ( !StringUtils.equals( existingLease.getIpAddress(), newLease.getIpAddress() ) )
        {
            logger.warn( "In isLatestLease, new lease's IP address doesn't match with existing lease's IP address." );
            return false;
        }
        if ( !StringUtils.equals( existingLease.getMacAddress(), newLease.getMacAddress() ) )
        {
            logger.warn( "In isLatestLease, new lease's MAC address doesn't match with existing lease's MAC address." );
            return false;
        }

        Date existingLeaseEndsDate = parseDate( existingLease.getEnds() );
        if ( existingLeaseEndsDate == null )
        {
            logger.error( "In isLatestLease, error parsing existing lease's ends date - '{}'.",
                          existingLease.getEnds() );
            return false;
        }
        Date newLeaseEndsDate = parseDate( newLease.getEnds() );
        if ( newLeaseEndsDate == null )
        {
            logger.error( "In isLatestLease, error parsing existing lease's ends date - '{}'.", newLease.getEnds() );
            return false;
        }

        /*
         * Return true if new lease's ends date is AFTER the existing lease's ends date.
         */
        if ( newLeaseEndsDate.after( existingLeaseEndsDate ) )
        {
            return true;
        }
        return false;
    }

    /**
     * Parses the date.
     *
     * @param dateString the date string
     * @return the date
     */
    private static Date parseDate( final String dateString )
    {
        try
        {
            return simpleDateFormat.parse( dateString );
        }
        catch ( ParseException e )
        {
            logger.error( "In parseDate, error parsing '{}' as '{}'.", dateString, DHCP_LEASE_DATE_PATTERN, e );
        }
        return null;
    }
}
