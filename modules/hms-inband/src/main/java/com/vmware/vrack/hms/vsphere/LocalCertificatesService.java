/* ********************************************************************************
 * LocalCertificatesService.java
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
package com.vmware.vrack.hms.vsphere;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Access to the SSO related keystore and certificates User: konstantin
 */
public interface LocalCertificatesService
{
    public static final String SSO_USER_CERTIFICATE_ALIAS = "user-certificate";

    public static final String SSO_HOK_CERTIFICATE_ALIAS = "hok-certificate";

    public static final String EXTENSION_CLIENT_SSL_CERTIFICATE_ALIAS = "extension-certificate";

    public static final String SSO_WEB_CERTIFICATE_ALIAS = "websso-certificate";

    public static final String PRINCIPAL_NAME = "LanierCM";

    public static final int CA_VALIDITY_DAYS = 365 * 30; // 30 years...

    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    public static final int KEY_SIZE = 2048;

    public static final char API_PASSWORD[] = "API-Password".toCharArray();

    public X509Certificate getWebSSOCertificate();

    public PrivateKey getWebSSOPrivateKey();

    public X509Certificate getUserCertificate();

    public PrivateKey getUserPrivateKey();

    public String getSolutionPrincipalName();

    public X509Certificate getHoKCertificate();

    public PrivateKey getHoKPrivateKey();

    public X509Certificate getExtensionSSLClientAuthenticationCertificate();

    public PrivateKey getExtensionSSLClientAuthenticationKey();

    public String getExtensionSSLCN();

    public KeyStore getKeyStore();

    public void deleteKeystore();

    public void deleteFederation();
}
