package com.francelabs.realm;

import java.security.Principal;
import java.security.cert.X509Certificate;

import org.apache.catalina.Realm;
import org.apache.catalina.realm.CombinedRealm;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class CustomCombinedRealm extends CombinedRealm{

	private static final Logger log = Logger.getLogger(CombinedRealm.class);
	
	 /**
     * Return the Principal associated with the specified username and
     * credentials, if there is one; otherwise return <code>null</code>.
     *
     * @param username Username of the Principal to look up
     * @param credentials Password or other credentials to use in
     *  authenticating this username
     */
    @Override
    public Principal authenticate(String username, String credentials) {
    	BasicConfigurator.configure();
        GenericPrincipal authenticatedUser = null;
        GenericPrincipal newauthenticatedUser = null;
        for (Realm realm : realms) {
           if (log.isDebugEnabled()) {
                log.debug(sm.getString("combinedRealm.authStart", username,
                        realm.getClass().getName()));
            }

            authenticatedUser = (GenericPrincipal) realm.authenticate(username, credentials);

            if (authenticatedUser == null) {
            	// if authentification fails
            	newauthenticatedUser = authenticatedUser;
            		if (log.isDebugEnabled()) {
                    log.debug(sm.getString("combinedRealm.authFail", username,
                            realm.getClass().getName()));
            	}
            } else {
            	// else we get the role of the user in MongoDb
            	RoleGetter roleGetter = new RoleGetter(authenticatedUser.getName(),(new MongoDBRunning(User.IDENTIFIERSDB).getDb()));
            	// then we create a new principal with the roles that we got form MongoDB
            	newauthenticatedUser = new GenericPrincipal(authenticatedUser.getName(),authenticatedUser.getPassword(),roleGetter.getRoles());
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("combinedRealm.authSuccess",
                            username, realm.getClass().getName()));
                }
                break;
            }
        }
        return newauthenticatedUser;
    }
    
    /**
     * Return the Principal associated with the specified chain of X509
     * client certificates.  If there is none, return <code>null</code>.
     *
     * @param certs Array of client certificates, with the first one in
     *  the array being the certificate of the client itself.
     */
    @Override
    public Principal authenticate(X509Certificate[] certs) {
    	   GenericPrincipal authenticatedUser = null;
           GenericPrincipal newauthenticatedUser = null;
           String username = null;
        if (certs != null && certs.length >0) {
            username = certs[0].getSubjectDN().getName();
        }
        
        for (Realm realm : realms) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("combinedRealm.authStart", username, realm.getInfo()));
            }

            authenticatedUser = (GenericPrincipal) realm.authenticate(certs);

            if (authenticatedUser == null) {
            	//if authentification fails
            	newauthenticatedUser = authenticatedUser;
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("combinedRealm.authFail", username,
                            realm.getClass().getName()));
                }
            } else {
            	// else we get the role of the user in MongoDb
            	RoleGetter roleGetter = new RoleGetter(authenticatedUser.getName(),(new MongoDBRunning(User.IDENTIFIERSDB).getDb()));
            	// then we create a new principal with the roles that we got form MongoDB
            	newauthenticatedUser = new GenericPrincipal(authenticatedUser.getName(),authenticatedUser.getPassword(),roleGetter.getRoles());
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("combinedRealm.authSuccess",
                            username, realm.getClass().getName()));
                }
                break;
            }
        }
        return newauthenticatedUser;
    }
    
    /**
     * Return the Principal associated with the specified username, which
     * matches the digest calculated using the given parameters using the
     * method described in RFC 2069; otherwise return <code>null</code>.
     *
     * @param username Username of the Principal to look up
     * @param clientDigest Digest which has been submitted by the client
     * @param nonce Unique (or supposedly unique) token which has been used
     * for this request
     * @param realmName Realm name
     * @param md5a2 Second MD5 digest used to calculate the digest :
     * MD5(Method + ":" + uri)
     */
    @Override
    public Principal authenticate(String username, String clientDigest,
            String nonce, String nc, String cnonce, String qop,
            String realmName, String md5a2) {
    	GenericPrincipal authenticatedUser,newauthenticatedUser=null;
        
        for (Realm realm : realms) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("combinedRealm.authStart", username, realm.getInfo()));
            }

            authenticatedUser = (GenericPrincipal) realm.authenticate(username, clientDigest, nonce,
                    nc, cnonce, qop, realmName, md5a2);


            if (authenticatedUser == null) {
            	// if authentification fails
            	newauthenticatedUser = authenticatedUser;
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("combinedRealm.authFail", username,
                            realm.getClass().getName()));
                }
            } else {
            	// else we get the role of the user in MongoDb
            	RoleGetter roleGetter = new RoleGetter(authenticatedUser.getName(),(new MongoDBRunning(User.IDENTIFIERSDB).getDb())); 
            	// then we create a new principal with the roles that we got form MongoDB
            	newauthenticatedUser = new GenericPrincipal(authenticatedUser.getName(),authenticatedUser.getPassword(),roleGetter.getRoles());
            	if (log.isDebugEnabled()) {
                    log.debug(sm.getString("combinedRealm.authSuccess",
                            username, realm.getClass().getName()));
                }
                break;
            }
        }
        return newauthenticatedUser;
    }


}
