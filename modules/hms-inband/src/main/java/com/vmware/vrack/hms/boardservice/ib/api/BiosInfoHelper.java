/* ********************************************************************************
 * BiosInfoHelper.java
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
package com.vmware.vrack.hms.boardservice.ib.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vmware.vim.binding.vim.host.BIOSInfo;
import com.vmware.vim.binding.vim.host.HardwareInfo;
import com.vmware.vrack.hms.common.servernodes.api.bios.BiosInfo;

/**
 * Bios Info Helper
 * 
 * @author Vishesh Nirwal
 */
public class BiosInfoHelper
{
    public static BiosInfo getBiosInfo( HardwareInfo hardwareInfo )
        throws Exception
    {
        if ( hardwareInfo != null )
        {
            BiosInfo biosInfo = new BiosInfo();
            BIOSInfo bInfo = hardwareInfo.getBiosInfo();
            if ( bInfo != null )
            {
                biosInfo.setBiosVersion( bInfo.getBiosVersion() );
                DateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd_hh:mm:ss" );
                if ( bInfo.getReleaseDate() != null )
                {
                    Date date = bInfo.getReleaseDate().getTime();
                    String biosReleaseDate = formatter.format( date );
                    biosInfo.setBiosReleaseDate( biosReleaseDate );
                }
            }
            return biosInfo;
        }
        else
        {
            throw new Exception( "Can not get Bios Info because the Hardware info object is NULL" );
        }
    }
}
