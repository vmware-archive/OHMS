/* ********************************************************************************
 * EsxiSshUtilTest.java
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
package com.vmware.vrack.hms.common.util;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.Properties;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Test class for {@link EsxiSshUtil}
 *
 * @author spolepalli
 */
@RunWith( PowerMockRunner.class )
@FixMethodOrder( MethodSorters.NAME_ASCENDING )
@PrepareForTest( EsxiSshUtil.class )
public class EsxiSshUtilTest
{

    @Test
    public void testGetSessionOnSuccessCase()
        throws Exception
    {

        // Mock objects
        File fileMock = mock( File.class );
        whenNew( File.class ).withArguments( anyString() ).thenReturn( fileMock );
        when( fileMock.exists() ).thenReturn( true );
        when( fileMock.isDirectory() ).thenReturn( false );

        Session session = mock( Session.class );
        JSch jsch = mock( JSch.class );
        whenNew( JSch.class ).withNoArguments().thenReturn( jsch );
        Mockito.doNothing().when( jsch ).setKnownHosts( anyString() );
        when( jsch.getSession( anyString(), anyString(), anyInt() ) ).thenReturn( session );

        // PowerMockito.mockStatic( StaticContextAccessor.class );
        // KeyStorePasswordProvider keyStorePasswordProvider = mock( KeyStorePasswordProvider.class );
        // when( StaticContextAccessor.getBean( KeyStorePasswordProvider.class ) ).thenReturn( keyStorePasswordProvider
        // );
        // when( keyStorePasswordProvider.getPassword() ).thenReturn( "****" );
        //
        // PowerMockito.mockStatic( CipherServiceUtil.class );
        // when( CipherServiceUtil.getDecryptedProperty( any( String.class ) ) ).thenReturn( "****" );

        Properties config = new Properties();
        config.put( "StrictHostKeyChecking", "yes" );
        Session sessionObj = EsxiSshUtil.getSessionObject( "user", "password", "127.0.0.1", 22, config );
        assertNotNull( sessionObj );
    }
}
