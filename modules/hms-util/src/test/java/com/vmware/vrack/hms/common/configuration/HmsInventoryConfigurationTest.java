/* ********************************************************************************
 * HmsInventoryConfigurationTest.java
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
package com.vmware.vrack.hms.common.configuration;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.util.HmsGenericUtil;

public class HmsInventoryConfigurationTest
{

    private static Logger logger = Logger.getLogger( HmsInventoryConfigurationTest.class );

    @Test
    public void test()
        throws IOException, HmsException
    {
        logger.info( "Testing HmsInventoryConfigurationTest" );
        File file = null;
        final String fileName = "test-inventory.json";
        file = new File( fileName );
        file.createNewFile();
        HmsInventoryConfiguration hmsInventoryConfiguration = new HmsInventoryConfiguration();
        hmsInventoryConfiguration.setFilename( fileName );
        assertNotNull( hmsInventoryConfiguration.getFilename() );
        hmsInventoryConfiguration.store( hmsInventoryConfiguration.getFilename() );
        file.delete();
        HmsGenericUtil.deleteLatestInventoryBackup();
    }
}
