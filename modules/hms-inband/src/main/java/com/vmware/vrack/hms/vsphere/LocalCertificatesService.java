/* ********************************************************************************
 * LocalCertificatesService.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
