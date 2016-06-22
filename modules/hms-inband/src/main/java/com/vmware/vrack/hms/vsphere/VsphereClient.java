/* ********************************************************************************
 * VsphereClient.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.vsphere;

import java.io.Closeable;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim.binding.impl.vmodl.TypeNameImpl;
import com.vmware.vim.binding.vim.ServiceInstance;
import com.vmware.vim.binding.vim.ServiceInstanceContent;
import com.vmware.vim.binding.vim.SessionManager;
import com.vmware.vim.binding.vim.UserSession;
import com.vmware.vim.binding.vim.version.internal.version9;
import com.vmware.vim.binding.vim.view.ViewManager;
import com.vmware.vim.binding.vmodl.ManagedObject;
import com.vmware.vim.binding.vmodl.ManagedObjectReference;
import com.vmware.vim.binding.vmodl.TypeName;
import com.vmware.vim.binding.vmodl.query.PropertyCollector;
import com.vmware.vim.vmomi.client.Client;
import com.vmware.vim.vmomi.client.common.ProtocolBinding;
import com.vmware.vim.vmomi.client.http.HttpClientConfiguration;
import com.vmware.vim.vmomi.client.http.HttpConfiguration;
import com.vmware.vim.vmomi.client.http.ThumbprintVerifier;
import com.vmware.vim.vmomi.client.http.impl.HttpConfigurationImpl;
import com.vmware.vim.vmomi.core.impl.BlockingFuture;
import com.vmware.vim.vmomi.core.types.VmodlContext;

/**
 * Author: Tao Ma Date: 2/21/14
 */
public class VsphereClient
    implements Client, AutoCloseable, Closeable
{
    /*
     * Re-factor hard coded string 2014-08-07
     */
    private static final String VIEW_MANAGER_IS_NULL_FOR = "ViewManager is null for %s.";

    private static final String PROPERTY_COLLECTOR_IS_NULL_FOR = "PropertyCollector is null for %s.";

    private static final String NO_CONNECTION_TO_V_SPHERE = "No connection to vSphere.";

    private static final String EN = "en";

    private static final String SERVICE_INSTANCE = "ServiceInstance";

    private static final String HTTP = "http";

    private static final String COM_VMWARE_VIM_BINDING_VMODL_REFLECT = "com.vmware.vim.binding.vmodl.reflect";

    private static final String COM_VMWARE_VIM_BINDING_VIM = "com.vmware.vim.binding.vim";

    private static final Logger logger = LoggerFactory.getLogger( VsphereClient.class );

    private ThreadPoolExecutor executor;

    private HttpConfiguration httpConfig;

    private Client client;

    private String uri;

    private ServiceInstanceContent sic;

    private SessionManager sessionMgr;

    private UserSession userSession;

    private PropertyCollector propertyCollector;

    private ViewManager viewManager;

    private static final String https_sdk_tunnel_8089 = "https://sdkTunnel:8089";

    // This is indicate if the client is connected as an extension.
    private boolean extension = false;

    // If connected as an extension then the extension key being used.
    private String extensionKey = null;

    static
    {
        VmodlContext context = VmodlContext.initContext( new String[] { COM_VMWARE_VIM_BINDING_VIM,
            COM_VMWARE_VIM_BINDING_VMODL_REFLECT } );
    }

    private VsphereClient( String uri, String name, String password, LocalCertificatesService cert )
        throws Exception
    {
        this.uri = uri;
        URI serviceUri = new URI( uri );
        executor = new ThreadPoolExecutor( 1, // core pool size
                                           1, // max pool size
                                           10, TimeUnit.SECONDS, // max thread idle time
                                           new LinkedBlockingQueue<Runnable>() ); // work queue
        HttpClientConfiguration clientConfig = HttpClientConfiguration.Factory.newInstance();
        httpConfig = new HttpConfigurationImpl();
        httpConfig.setThumbprintVerifier( new ThumbprintVerifier()
        {
            @Override
            public Result verify( String thumbprint )
            {
                // TODO implement thumbprint verification logic
                return Result.MATCH; // ignore verify
            }

            @Override
            public void onSuccess( X509Certificate[] chain, String thumbprint, Result verifyResult,
                                   boolean trustedChain, boolean verifiedAssertions )
                                       throws SSLException
            {
                // this callback let's application know if
                // server cert was accepted by trust store or thumbrint
                // and also if hostname verification was successfull
            }
        } );
        if ( cert != null )
        {
            httpConfig.setKeyStore( cert.getKeyStore() );
            httpConfig.getKeyStoreConfig().setKeyAlias( LocalCertificatesService.EXTENSION_CLIENT_SSL_CERTIFICATE_ALIAS );
            httpConfig.getKeyStoreConfig().setKeyPassword( new String( LocalCertificatesService.API_PASSWORD ) );
            httpConfig.getKeyStoreConfig().setKeyStorePassword( new String( LocalCertificatesService.API_PASSWORD ) );
            httpConfig.setDefaultProxy( serviceUri.getHost(), 80, HTTP );
        }
        clientConfig.setExecutor( executor );
        clientConfig.setHttpConfiguration( httpConfig );
        client = Client.Factory.createClient( ( cert != null ? new URI( https_sdk_tunnel_8089 ) : serviceUri ),
                                              version9.class, clientConfig );
        ManagedObjectReference svcRef = new ManagedObjectReference();
        svcRef.setType( SERVICE_INSTANCE );
        svcRef.setValue( SERVICE_INSTANCE );
        ServiceInstance si = client.createStub( ServiceInstance.class, svcRef );
        sic = si.retrieveContent();
        sessionMgr = client.createStub( SessionManager.class, sic.getSessionManager() );
        if ( cert == null )
        {
            // user
            BlockingFuture<UserSession> userSessionFuture = new BlockingFuture<UserSession>();
            sessionMgr.login( name, password, null, userSessionFuture );
            userSession = userSessionFuture.get();
        }
        else
        {
            logger.debug( "Creating the vSphere Client for URI: " + uri );
            // extension
            userSession = sessionMgr.loginExtensionByCertificate( cert.getExtensionSSLCN(), EN );
            extension = true;
            extensionKey = cert.getExtensionSSLCN();
        }
        logger.info( "Successfully login to {}", uri );
    }

    public static VsphereClient connect( String uri, String username, String password )
        throws Exception
    {
        return new VsphereClient( uri, username, password, null );
    }

    public static VsphereClient connect( String uri, LocalCertificatesService service )
        throws Exception
    {
        return new VsphereClient( uri, null, null, service );
    }

    public Client getClient()
    {
        return client;
    }

    public UserSession getUserSession()
    {
        return userSession;
    }

    public boolean isExtension()
    {
        return extension;
    }

    public String getExtensionKey()
    {
        if ( extension )
            return extensionKey;
        else
            return null;
    }

    @Override
    public <T extends ManagedObject> T createStub( Class<T> clazz, ManagedObjectReference moRef )
    {
        return client.createStub( clazz, moRef );
    }

    @Override
    public ProtocolBinding getBinding()
    {
        return client.getBinding();
    }

    @Override
    public void shutdown()
    {
        sessionMgr.logout();
        client.shutdown();
        executor.shutdown();
    }

    public ServiceInstanceContent getServiceInstanceContent()
    {
        // TODO: define more specific exception.
        if ( null == client )
            throw new NullPointerException( NO_CONNECTION_TO_V_SPHERE );
        return sic;
    }

    public PropertyCollector getPropertyCollector()
    {
        if ( null == getServiceInstanceContent().getPropertyCollector() )
            throw new NullPointerException( String.format( PROPERTY_COLLECTOR_IS_NULL_FOR, uri ) );
        if ( null == propertyCollector )
            propertyCollector = createStub( PropertyCollector.class, sic.getPropertyCollector() );
        return propertyCollector;
    }

    public ViewManager getViewManager()
    {
        if ( null == getServiceInstanceContent().getViewManager() )
            throw new NullPointerException( String.format( VIEW_MANAGER_IS_NULL_FOR, uri ) );
        if ( null == viewManager )
            viewManager = createStub( ViewManager.class, sic.getViewManager() );
        return viewManager;
    }

    public ManagedObjectReference getRootRef()
    {
        return getServiceInstanceContent().getRootFolder();
    }

    public ManagedObjectReference getContainerView( String typeName )
    {
        return getViewManager().createContainerView( getRootRef(), new TypeName[] { new TypeNameImpl( typeName ) },
                                                     true );
    }

    @Override
    public void close()
    {
        this.shutdown();
    }
}
