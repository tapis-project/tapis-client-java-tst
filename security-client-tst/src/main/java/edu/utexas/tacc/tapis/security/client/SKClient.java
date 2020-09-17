package edu.utexas.tacc.tapis.security.client;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.utexas.tacc.tapis.client.shared.Utils;
import edu.utexas.tacc.tapis.client.shared.exceptions.TapisClientException;
import edu.utexas.tacc.tapis.security.client.gen.ApiClient;
import edu.utexas.tacc.tapis.security.client.gen.ApiException;
import edu.utexas.tacc.tapis.security.client.gen.api.GeneralApi;
import edu.utexas.tacc.tapis.security.client.gen.api.RoleApi;
import edu.utexas.tacc.tapis.security.client.gen.api.UserApi;
import edu.utexas.tacc.tapis.security.client.gen.api.VaultApi;
import edu.utexas.tacc.tapis.security.client.gen.model.Options;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqAddChildRole;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqAddRolePermission;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqCreateRole;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqGrantUserPermission;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqGrantUserRole;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqGrantUserRoleWithPermission;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqPreviewPathPrefix;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqRemoveChildRole;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqRemoveRolePermission;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqReplacePathPrefix;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqRevokeUserPermission;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqRevokeUserRole;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqUpdateRoleDescription;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqUpdateRoleName;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqUpdateRoleOwner;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqUserHasRole;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqUserHasRoleMulti;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqUserIsPermitted;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqUserIsPermittedMulti;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqValidateServicePwd;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqVersions;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqWriteSecret;
import edu.utexas.tacc.tapis.security.client.gen.model.RespAuthorized;
import edu.utexas.tacc.tapis.security.client.gen.model.RespBasic;
import edu.utexas.tacc.tapis.security.client.gen.model.RespChangeCount;
import edu.utexas.tacc.tapis.security.client.gen.model.RespName;
import edu.utexas.tacc.tapis.security.client.gen.model.RespNameArray;
import edu.utexas.tacc.tapis.security.client.gen.model.RespPathPrefixes;
import edu.utexas.tacc.tapis.security.client.gen.model.RespProbe;
import edu.utexas.tacc.tapis.security.client.gen.model.RespResourceUrl;
import edu.utexas.tacc.tapis.security.client.gen.model.RespRole;
import edu.utexas.tacc.tapis.security.client.gen.model.RespSecret;
import edu.utexas.tacc.tapis.security.client.gen.model.RespSecretList;
import edu.utexas.tacc.tapis.security.client.gen.model.RespSecretMeta;
import edu.utexas.tacc.tapis.security.client.gen.model.RespSecretVersionMetadata;
import edu.utexas.tacc.tapis.security.client.gen.model.RespVersions;
import edu.utexas.tacc.tapis.security.client.gen.model.SkRole;
import edu.utexas.tacc.tapis.security.client.gen.model.SkSecret;
import edu.utexas.tacc.tapis.security.client.gen.model.SkSecretList;
import edu.utexas.tacc.tapis.security.client.gen.model.SkSecretMetadata;
import edu.utexas.tacc.tapis.security.client.gen.model.SkSecretVersionMetadata;
import edu.utexas.tacc.tapis.security.client.gen.model.Transformation;
import edu.utexas.tacc.tapis.security.client.model.SKSecretDeleteParms;
import edu.utexas.tacc.tapis.security.client.model.SKSecretMetaParms;
import edu.utexas.tacc.tapis.security.client.model.SKSecretReadParms;
import edu.utexas.tacc.tapis.security.client.model.SKSecretWriteParms;

public class SKClient 
{
    /* **************************************************************************** */
    /*                                   Constants                                  */
    /* **************************************************************************** */
    // Response status.
    public static final String STATUS_SUCCESS = "success";
    
    // Header keys for tapis.
    public static final String TAPIS_JWT_HEADER  = "X-Tapis-Token";
    public static final String TAPIS_JWT_TENANT  = "X-Tapis-Tenant";
    public static final String TAPIS_JWT_USER    = "X-Tapis-User";
    public static final String TAPIS_HASH_HEADER = "X-Tapis-User-Token-Hash";
    
    // Configuration defaults.
    private static final String SKCLIENT_USER_AGENT = "SKClient";
    
    /* **************************************************************************** */
    /*                                    Fields                                    */
    /* **************************************************************************** */
    // This client instance's underlying generated client.
    private final ApiClient _apiClient;
    
    /* **************************************************************************** */
    /*                                 Constructors                                 */
    /* **************************************************************************** */
    /* ---------------------------------------------------------------------------- */
    /* constructor:                                                                 */
    /* ---------------------------------------------------------------------------- */
    /** Constructor that uses the compiled-in basePath value in ApiClient.  This
     * constructor is only appropriate for test code.
     */
    public SKClient() {this(null, null);}
    
    /* ---------------------------------------------------------------------------- */
    /* constructor:                                                                 */
    /* ---------------------------------------------------------------------------- */
    /** Constructor that overrides the compiled-in basePath value in ApiClient.  This
     * constructor typically used in production.
     * 
     * The path includes the URL prefix up to and including the service root.  By
     * default this value is http://localhost:8080/v3.  In more production-like
     * environments the protocol will be https and the host/port will be specific to 
     * that environment.  For example, a development environment might define its
     * base url as https://tenant1.develop.tapis.io/v3.
     * 
     * The jwt is the base64url representation of a Tapis JWT.  If not null or empty,
     * the TAPIS_JWT_HEADER key will be set to the jwt value. 
     * 
     * The user-agent is automatically set to SKClient.
     * 
     * Instances of this class are currently limited to using the default ApiClient.
     * This implies that the RoleApi, UserApi and GeneralApi implementations also
     * are expected to be using the same default ApiClient object.
     * 
     * @param path the base path 
     */
    public SKClient(String path, String jwt) 
    {
    	// Create actual client.
    	_apiClient = new ApiClient();
    	
        // Process input.
        if (!StringUtils.isBlank(path)) _apiClient.setBasePath(path);
        if (!StringUtils.isBlank(jwt))  _apiClient.addDefaultHeader(TAPIS_JWT_HEADER, jwt);
        
        // Other defaults.
        _apiClient.setUserAgent(SKCLIENT_USER_AGENT);
    }
    
    /* **************************************************************************** */
    /*                                Utility Methods                               */
    /* **************************************************************************** */
    /* ---------------------------------------------------------------------------- */
    /* setBasePath:                                                                 */
    /* ---------------------------------------------------------------------------- */
    public SKClient setBasePath(String path)
    {
        _apiClient.setBasePath(path);
        return this;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* addDefaultHeader:                                                            */
    /* ---------------------------------------------------------------------------- */
    public SKClient addDefaultHeader(String key, String value)
    {
        _apiClient.addDefaultHeader(key, value);
        return this;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* setUserAgent:                                                                */
    /* ---------------------------------------------------------------------------- */
    public SKClient setUserAgent(String userAgent) 
    {
        _apiClient.setUserAgent(userAgent);
        return this;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* setConnectTimeout:                                                           */
    /* ---------------------------------------------------------------------------- */
    /** Set the connection timeout
     * 
     * @param millis the connection timeout in milliseconds; 0 means forever.
     * @return this object
     */
    public SKClient setConnectTimeout(int millis)
    {
        _apiClient.setConnectTimeout(millis);
        return this;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* setReadTimeout:                                                              */
    /* ---------------------------------------------------------------------------- */
    /** Set the read timeout
     * 
     * @param millis the read timeout in milliseconds; 0 means forever.
     * @return this object
     */
    public SKClient setReadTimeout(int millis)
    {
        _apiClient.setReadTimeout(millis);
        return this;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* setDebugging:                                                                */
    /* ---------------------------------------------------------------------------- */
    public SKClient setDebugging(boolean debugging) 
    {
        _apiClient.setDebugging(debugging);
        return this;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* getConnectTimeout:                                                           */
    /* ---------------------------------------------------------------------------- */
    /** Get the connection timeout.
     * 
     * @return the connection timeout in milliseconds
     */
    public int getConnectTimeout()
    {
        return _apiClient.getConnectTimeout();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* getReadTimeout:                                                              */
    /* ---------------------------------------------------------------------------- */
    /** Get the read timeout.
     * 
     * @return read timeout in milliseconds
     */
    public int getReadTimeout()
    {
        return _apiClient.getReadTimeout();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* isDebugging:                                                                 */
    /* ---------------------------------------------------------------------------- */
    public boolean isDebugging() 
    {
        return _apiClient.isDebugging();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* close:                                                                       */
    /* ---------------------------------------------------------------------------- */
    /** Close connections and stop threads that can sometimes prevent JVM shutdown.
     */
    public void close()
    {
        try {
            // Best effort attempt to shut things down.
            var okClient = _apiClient.getHttpClient();
            if (okClient != null) {
                var pool = okClient.connectionPool();
                if (pool != null) pool.evictAll();
            }
        } catch (Exception e) {}      
    }
    
    /* **************************************************************************** */
    /*                              Public Role Methods                             */
    /* **************************************************************************** */
    /* ---------------------------------------------------------------------------- */
    /* getRoleNames:                                                                */
    /* ---------------------------------------------------------------------------- */
    public List<String> getRoleNames(String tenant)
     throws TapisClientException
    {
        // Make the REST call.
        RespNameArray resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.getRoleNames(tenant, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult().getNames();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* getRoleByName:                                                               */
    /* ---------------------------------------------------------------------------- */
    public SkRole getRoleByName(String tenant, String roleName)
     throws TapisClientException
    {
        // Make the REST call.
        RespRole resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.getRoleByName(roleName, tenant, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* createRole:                                                                  */
    /* ---------------------------------------------------------------------------- */
    public String createRole(String roleTenant, String roleName, String description)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqCreateRole();
        body.setRoleTenant(roleTenant);
        body.setRoleName(roleName);
        body.setDescription(description);
        
        // Make the REST call.
        RespResourceUrl resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.createRole(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult().getUrl();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* deleteRoleByName:                                                            */
    /* ---------------------------------------------------------------------------- */
    public int deleteRoleByName(String tenant, String roleName)
     throws TapisClientException
    {
        // Make the REST call.
        RespChangeCount resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.deleteRoleByName(roleName, tenant, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Integer x = resp.getResult().getChanges();
        return x == null ? 0 : x;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* updateRoleName:                                                              */
    /* ---------------------------------------------------------------------------- */
    public void updateRoleName(String roleTenant, String roleName, String newRoleName)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqUpdateRoleName();
        body.setRoleTenant(roleTenant);
        body.setNewRoleName(newRoleName);
        
        // Make the REST call.
        @SuppressWarnings("unused")
        RespBasic resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.updateRoleName(roleName, body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
    }
    
    /* ---------------------------------------------------------------------------- */
    /* updateRoleOwner:                                                             */
    /* ---------------------------------------------------------------------------- */
    public void updateRoleOwner(String tenant, String roleName, String newOwner)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqUpdateRoleOwner();
        body.setRoleTenant(tenant);
        body.setNewOwner(newOwner);
        
        // Make the REST call.
        @SuppressWarnings("unused")
        RespBasic resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.updateRoleOwner(roleName, body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
    }
    
    /* ---------------------------------------------------------------------------- */
    /* updateRoleDescription:                                                       */
    /* ---------------------------------------------------------------------------- */
    public void updateRoleDescription(String roleTenant, String roleName, String newDescription)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqUpdateRoleDescription();
        body.setRoleTenant(roleTenant);
        body.setNewDescription(newDescription);
        
        // Make the REST call.
        @SuppressWarnings("unused")
        RespBasic resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.updateRoleDescription(roleName, body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
    }
    
    /* ---------------------------------------------------------------------------- */
    /* getRolePermissions:                                                          */
    /* ---------------------------------------------------------------------------- */
    public List<String> getRolePermissions(String roleTenant, String roleName, boolean immediate)
     throws TapisClientException
    {
        // Make the REST call.
    	RespNameArray resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.getRolePermissions(roleName, roleTenant, immediate, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        // Return result value.
        return resp.getResult().getNames();
    }
    
   /* ---------------------------------------------------------------------------- */
    /* addRolePermission:                                                           */
    /* ---------------------------------------------------------------------------- */
    public int addRolePermission(String roleTenant, String roleName, String permSpec)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqAddRolePermission();
        body.setRoleTenant(roleTenant);
        body.setRoleName(roleName);
        body.setPermSpec(permSpec);
        
        // Make the REST call.
        RespChangeCount resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.addRolePermission(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Integer x = resp.getResult().getChanges();
        return x == null ? 0 : x;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* removeRolePermission:                                                        */
    /* ---------------------------------------------------------------------------- */
    public int removeRolePermission(String roleTenant, String roleName, String permSpec)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqRemoveRolePermission();
        body.setRoleTenant(roleTenant);
        body.setRoleName(roleName);
        body.setPermSpec(permSpec);
        
        // Make the REST call.
        RespChangeCount resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.removeRolePermission(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Integer x = resp.getResult().getChanges();
        return x == null ? 0 : x;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* addChildRole:                                                                */
    /* ---------------------------------------------------------------------------- */
    public int addChildRole(String roleTenant, String parentRoleName, String childRoleName)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqAddChildRole();
        body.setRoleTenant(roleTenant);
        body.setParentRoleName(parentRoleName);
        body.setChildRoleName(childRoleName);
        
        // Make the REST call.
        RespChangeCount resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.addChildRole(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Integer x = resp.getResult().getChanges();
        return x == null ? 0 : x;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* removeChildRole:                                                             */
    /* ---------------------------------------------------------------------------- */
    public int removeChildRole(String roleTenant, String parentRoleName, String childRoleName)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqRemoveChildRole();
        body.setRoleTenant(roleTenant);
        body.setParentRoleName(parentRoleName);
        body.setChildRoleName(childRoleName);
        
        // Make the REST call.
        RespChangeCount resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.removeChildRole(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Integer x = resp.getResult().getChanges();
        return x == null ? 0 : x;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* previewPathPrefix:                                                           */
    /* ---------------------------------------------------------------------------- */
    public List<Transformation> previewPathPrefix(String tenant, String schema, String roleName,
                                                  String oldSystemId, String newSystemId,
                                                  String oldPrefix, String newPrefix)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqPreviewPathPrefix();
        body.setTenant(tenant);
        body.setSchema(schema);
        body.setRoleName(roleName);
        body.setOldSystemId(oldSystemId);
        body.setNewSystemId(newSystemId);
        body.setOldPrefix(oldPrefix);
        body.setNewPrefix(newPrefix);
        
        // Make the REST call.
        RespPathPrefixes resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.previewPathPrefix(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* replacePathPrefix:                                                           */
    /* ---------------------------------------------------------------------------- */
    public int replacePathPrefix(String tenant, String schema, String roleName,
                                 String oldSystemId, String newSystemId,
                                 String oldPrefix, String newPrefix)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqReplacePathPrefix();
        body.setTenant(tenant);
        body.setSchema(schema);
        body.setRoleName(roleName);
        body.setOldSystemId(oldSystemId);
        body.setNewSystemId(newSystemId);
        body.setOldPrefix(oldPrefix);
        body.setNewPrefix(newPrefix);
        
        // Make the REST call.
        RespChangeCount resp = null;
        try {
            // Get the API object using default networking.
            RoleApi roleApi = new RoleApi(_apiClient);
            resp = roleApi.replacePathPrefix(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Integer x = resp.getResult().getChanges();
        return x == null ? 0 : x;
    }
    
    /* **************************************************************************** */
    /*                              Public User Methods                             */
    /* **************************************************************************** */
    /* ---------------------------------------------------------------------------- */
    /* getUserNames:                                                                */
    /* ---------------------------------------------------------------------------- */
    public List<String> getUserNames(String tenant)
     throws TapisClientException
    {
        // Make the REST call.
        RespNameArray resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.getUserNames(tenant, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult().getNames();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* getUserRoles:                                                                */
    /* ---------------------------------------------------------------------------- */
    public List<String> getUserRoles(String tenant, String user)
     throws TapisClientException
    {
        // Make the REST call.
        RespNameArray resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.getUserRoles(user, tenant, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult().getNames();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* getUserPerms:                                                                */
    /* ---------------------------------------------------------------------------- */
    public List<String> getUserPerms(String tenant, String user)
     throws TapisClientException
    {
        return getUserPerms(tenant, user, null, null);
    }
    
    /* ---------------------------------------------------------------------------- */
    /* getUserPerms:                                                                */
    /* ---------------------------------------------------------------------------- */
    public List<String> getUserPerms(String tenant, String user, String implies, 
                                     String impliedBy)
     throws TapisClientException
    {
        // Make the REST call.
        RespNameArray resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.getUserPerms(user, tenant, implies, impliedBy, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult().getNames();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* grantUserRole:                                                               */
    /* ---------------------------------------------------------------------------- */
    public int grantUserRole(String tenant, String user, String roleName)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqGrantUserRole();
        body.setTenant(tenant);
        body.setUser(user);
        body.setRoleName(roleName);
        
        // Make the REST call.
        RespChangeCount resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.grantRole(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Integer x = resp.getResult().getChanges();
        return x == null ? 0 : x;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* revokeUserRole:                                                              */
    /* ---------------------------------------------------------------------------- */
    public int revokeUserRole(String tenant, String user, String roleName)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqRevokeUserRole();
        body.setTenant(tenant);
        body.setUser(user);
        body.setRoleName(roleName);
        
        // Make the REST call.
        RespChangeCount resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.revokeUserRole(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Integer x = resp.getResult().getChanges();
        return x == null ? 0 : x;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* grantRoleWithPermission:                                                     */
    /* ---------------------------------------------------------------------------- */
    public int grantRoleWithPermission(String tenant, String user, String roleName, 
                                       String permSpec)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqGrantUserRoleWithPermission();
        body.setTenant(tenant);
        body.setUser(user);
        body.setRoleName(roleName);
        body.setPermSpec(permSpec);
        
        // Make the REST call.
        RespChangeCount resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.grantRoleWithPermission(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Integer x = resp.getResult().getChanges();
        return x == null ? 0 : x;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* grantUserPermission:                                                         */
    /* ---------------------------------------------------------------------------- */
    public int grantUserPermission(String tenant, String user, String permSpec)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqGrantUserPermission();
        body.setTenant(tenant);
        body.setUser(user);
        body.setPermSpec(permSpec);
        
        // Make the REST call.
        RespChangeCount resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.grantUserPermission(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Integer x = resp.getResult().getChanges();
        return x == null ? 0 : x;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* revokeRoleWithPermission:                                                    */
    /* ---------------------------------------------------------------------------- */
    public int revokeUserPermission(String tenant, String user, String permSpec)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqRevokeUserPermission();
        body.setTenant(tenant);
        body.setUser(user);
        body.setPermSpec(permSpec);
        
        // Make the REST call.
        RespChangeCount resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.revokeUserPermission(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Integer x = resp.getResult().getChanges();
        return x == null ? 0 : x;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* hasRole:                                                                     */
    /* ---------------------------------------------------------------------------- */
    public boolean hasRole(String tenant, String user, String roleName)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqUserHasRole();
        body.setTenant(tenant);
        body.setUser(user);
        body.setRoleName(roleName);
        
        // Make the REST call.
        RespAuthorized resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.hasRole(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Boolean b = resp.getResult().getIsAuthorized();
        return b == null ? false : b;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* hasAnyRole:                                                                  */
    /* ---------------------------------------------------------------------------- */
    public boolean hasRoleAny(String tenant, String user, String[] roleNames)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqUserHasRoleMulti();
        body.setTenant(tenant);
        body.setUser(user);
        body.setRoleNames(Arrays.asList(roleNames));
        
        // Make the REST call.
        RespAuthorized resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.hasRoleAny(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Boolean b = resp.getResult().getIsAuthorized();
        return b == null ? false : b;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* hasAllRole:                                                                  */
    /* ---------------------------------------------------------------------------- */
    public boolean hasRoleAll(String tenant, String user, String[] roleNames)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqUserHasRoleMulti();
        body.setTenant(tenant);
        body.setUser(user);
        body.setRoleNames(Arrays.asList(roleNames));
        
        // Make the REST call.
        RespAuthorized resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.hasRoleAll(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Boolean b = resp.getResult().getIsAuthorized();
        return b == null ? false : b;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* isPermitted:                                                                 */
    /* ---------------------------------------------------------------------------- */
    public boolean isPermitted(String tenant, String user, String permSpec)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqUserIsPermitted();
        body.setTenant(tenant);
        body.setUser(user);
        body.setPermSpec(permSpec);
        
        // Make the REST call.
        RespAuthorized resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.isPermitted(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Boolean b = resp.getResult().getIsAuthorized();
        return b == null ? false : b;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* isPermittedAny:                                                              */
    /* ---------------------------------------------------------------------------- */
    public boolean isPermittedAny(String tenant, String user, String[] permSpecs)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqUserIsPermittedMulti();
        body.setTenant(tenant);
        body.setUser(user);
        body.setPermSpecs(Arrays.asList(permSpecs));
        
        // Make the REST call.
        RespAuthorized resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.isPermittedAny(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Boolean b = resp.getResult().getIsAuthorized();
        return b == null ? false : b;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* isPermittedAll:                                                              */
    /* ---------------------------------------------------------------------------- */
    public boolean isPermittedAll(String tenant, String user, String[] permSpecs)
     throws TapisClientException
    {
        // Assign input body.
        var body = new ReqUserIsPermittedMulti();
        body.setTenant(tenant);
        body.setUser(user);
        body.setPermSpecs(Arrays.asList(permSpecs));
        
        // Make the REST call.
        RespAuthorized resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.isPermittedAll(body, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Boolean b = resp.getResult().getIsAuthorized();
        return b == null ? false : b;
    }
    
    /* ---------------------------------------------------------------------------- */
    /* getUsersWithRole:                                                            */
    /* ---------------------------------------------------------------------------- */
    public List<String> getUsersWithRole(String tenant, String roleName)
     throws TapisClientException
    {
        // Make the REST call.
        RespNameArray resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.getUsersWithRole(roleName, tenant, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult().getNames();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* getUsersWithPermission:                                                      */
    /* ---------------------------------------------------------------------------- */
    public List<String> getUsersWithPermission(String tenant, String permSpec)
     throws TapisClientException
    {
        // Make the REST call.
        RespNameArray resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.getUsersWithPermission(permSpec, tenant, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult().getNames();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* getDefaultUserRole:                                                          */
    /* ---------------------------------------------------------------------------- */
    public String getDefaultUserRole(String user)
     throws TapisClientException
    {
        // Make the REST call.
        RespName resp = null;
        try {
            // Get the API object using default networking.
            var userApi = new UserApi(_apiClient);
            resp = userApi.getDefaultUserRole1(user, false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult().getName();
    }
    
    /* **************************************************************************** */
    /*                             Public Vault Methods                             */
    /* **************************************************************************** */
    /* ---------------------------------------------------------------------------- */
    /* readSecret:                                                                  */
    /* ---------------------------------------------------------------------------- */
    public SkSecret readSecret(SKSecretReadParms parms)
     throws TapisClientException
    {
        // Make the REST call.
        RespSecret resp = null;
        try {
            // Get the API object using default networking.
            var vaultApi = new VaultApi(_apiClient);
            resp = vaultApi.readSecret(parms.getSecretType().getUrlText(),
                                       parms.getSecretName(),
                                       parms.getTenant(),
                                       parms.getUser(),
                                       parms.getVersion(),
                                       false, // pretty
                                       parms.getSysId(),
                                       parms.getSysUser(),
                                       parms.getKeyType().name(),
                                       parms.getDbHost(),
                                       parms.getDbName(),
                                       parms.getDbService());
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* writeSecret:                                                                  */
    /* ---------------------------------------------------------------------------- */
    public SkSecretMetadata writeSecret(String tenant, String user, SKSecretWriteParms parms)
     throws TapisClientException
    {
        // Package the input.
        ReqWriteSecret reqWriteSecret = new ReqWriteSecret();
        reqWriteSecret.setTenant(tenant);
        reqWriteSecret.setUser(user);
        reqWriteSecret.setData(parms.getData());
        if (parms.getOptions() == null) reqWriteSecret.setOptions(new Options());
         else reqWriteSecret.setOptions(parms.getOptions());

        // Make the REST call.
        RespSecretMeta resp = null;
        try {
            // Get the API object using default networking.
            var vaultApi = new VaultApi(_apiClient);
            resp = vaultApi.writeSecret(parms.getSecretType().getUrlText(),
                                        parms.getSecretName(),
                                        reqWriteSecret,
                                        false, // pretty
                                        parms.getSysId(),
                                        parms.getSysUser(),
                                        parms.getKeyType().name(),
                                        parms.getDbHost(),
                                        parms.getDbName(),
                                        parms.getDbService());

        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* deleteSecret:                                                                */
    /* ---------------------------------------------------------------------------- */
    public List<Integer> deleteSecret(String tenant, String user, SKSecretDeleteParms parms)
     throws TapisClientException
    {
        // Package the input.
        ReqVersions reqVersions = new ReqVersions();
        reqVersions.setTenant(tenant);
        reqVersions.setUser(user);
        reqVersions.setVersions(parms.getVersions());

        // Make the REST call.
        RespVersions resp = null;
        try {
            // Get the API object using default networking.
            var vaultApi = new VaultApi(_apiClient);
            resp = vaultApi.deleteSecret(parms.getSecretType().getUrlText(),
                                         parms.getSecretName(),
                                         reqVersions,
                                         false, // pretty
                                         parms.getSysId(),
                                         parms.getSysUser(),
                                         parms.getKeyType().name(),
                                         parms.getDbHost(),
                                         parms.getDbName(),
                                         parms.getDbService());
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* undeleteSecret:                                                              */
    /* ---------------------------------------------------------------------------- */
    public List<Integer> undeleteSecret(String tenant, String user, SKSecretDeleteParms parms)
     throws TapisClientException
    {
        // Package the input.
        ReqVersions reqVersions = new ReqVersions();
        reqVersions.setTenant(tenant);
        reqVersions.setUser(user);
        reqVersions.setVersions(parms.getVersions());
        
        // Make the REST call.
        RespVersions resp = null;
        try {
            // Get the API object using default networking.
            var vaultApi = new VaultApi(_apiClient);
            resp = vaultApi.undeleteSecret(parms.getSecretType().getUrlText(),
                                           parms.getSecretName(),
                                           reqVersions,
                                           false, // pretty
                                           parms.getSysId(),
                                           parms.getSysUser(),
                                           parms.getKeyType().name(),
                                           parms.getDbHost(),
                                           parms.getDbName(),
                                           parms.getDbService());
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* destroySecret:                                                               */
    /* ---------------------------------------------------------------------------- */
    public List<Integer> destroySecret(String tenant, String user, SKSecretDeleteParms parms)
     throws TapisClientException
    {
        // Package the input.
        ReqVersions reqVersions = new ReqVersions();
        reqVersions.setTenant(tenant);
        reqVersions.setUser(user);
        reqVersions.setVersions(parms.getVersions());
        
        // Make the REST call.
        RespVersions resp = null;
        try {
            // Get the API object using default networking.
            var vaultApi = new VaultApi(_apiClient);
            resp = vaultApi.destroySecret(parms.getSecretType().getUrlText(),
                                          parms.getSecretName(),
                                          reqVersions,
                                          false, // pretty
                                          parms.getSysId(),
                                          parms.getSysUser(),
                                          parms.getKeyType().name(),
                                          parms.getDbHost(),
                                          parms.getDbName(),
                                          parms.getDbService());
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* readSecretMeta:                                                              */
    /* ---------------------------------------------------------------------------- */
    public SkSecretVersionMetadata readSecretMeta(SKSecretMetaParms parms)
     throws TapisClientException
    {
        // Make the REST call.
        RespSecretVersionMetadata resp = null;
        try {
            // Get the API object using default networking.
            var vaultApi = new VaultApi(_apiClient);
            resp = vaultApi.readSecretMeta(parms.getSecretType().getUrlText(),
                                           parms.getSecretName(),
                                           parms.getTenant(),
                                           parms.getUser(),
                                           false, // pretty
                                           parms.getSysId(),
                                           parms.getSysUser(),
                                           parms.getKeyType().name(),
                                           parms.getDbHost(),
                                           parms.getDbName(),
                                           parms.getDbService());
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* listSecretMeta:                                                              */
    /* ---------------------------------------------------------------------------- */
    public SkSecretList listSecretMeta(SKSecretMetaParms parms)
     throws TapisClientException
    {
        // Make the REST call.
        RespSecretList resp = null;
        try {
            // Get the API object using default networking.
            var vaultApi = new VaultApi(_apiClient);
            resp = vaultApi.listSecretMeta(parms.getSecretType().getUrlText(),
                                           parms.getTenant(),
                                           parms.getUser(),
                                           false, // pretty
                                           parms.getSysId(),
                                           parms.getSysUser(),
                                           parms.getKeyType().name(),
                                           parms.getDbHost(),
                                           parms.getDbName(),
                                           parms.getDbService());
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        return resp.getResult();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* destroySecretMeta:                                                           */
    /* ---------------------------------------------------------------------------- */
    public void destroySecretMeta(SKSecretMetaParms parms)
     throws TapisClientException
    {
        // Make the REST call.
        @SuppressWarnings("unused")
        RespBasic resp = null;
        try {
            // Get the API object using default networking.
            var vaultApi = new VaultApi(_apiClient);
            resp = vaultApi.destroySecretMeta(parms.getSecretType().getUrlText(),
                                              parms.getSecretName(),
                                              parms.getTenant(),
                                              parms.getUser(),
                                              false, // pretty
                                              parms.getSysId(),
                                              parms.getSysUser(),
                                              parms.getKeyType().name(),
                                              parms.getDbHost(),
                                              parms.getDbName(),
                                              parms.getDbService());
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
    }
    
    /* ---------------------------------------------------------------------------- */
    /* validateServicePassword:                                                     */
    /* ---------------------------------------------------------------------------- */
    public boolean validateServicePassword(String tenant, String user, 
                                           String serviceName, String password) 
     throws TapisClientException
    {
        // Initialize parameter.
        var reqValidateServicePwd = new ReqValidateServicePwd();
        reqValidateServicePwd.setTenant(tenant);
        reqValidateServicePwd.setUser(user);
        reqValidateServicePwd.setPassword(password);
        
        // Make the REST call.
        RespAuthorized resp = null;
        try {
            // Get the API object using default networking.
            var vaultApi = new VaultApi(_apiClient);
            resp = vaultApi.validateServicePassword(serviceName, 
                                                    reqValidateServicePwd, false);
            
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value.
        Boolean b = resp.getResult().getIsAuthorized();
        return b == null ? false : b;
    }
    
    /* **************************************************************************** */
    /*                            Public General Methods                            */
    /* **************************************************************************** */
    /* ---------------------------------------------------------------------------- */
    /* hello:                                                                       */
    /* ---------------------------------------------------------------------------- */
    public String hello()
     throws TapisClientException
    {
        // Make the REST call.
        RespBasic resp = null;
        try {
            // Get the API object using default networking.
            var generalApi = new GeneralApi(_apiClient);
            resp = generalApi.sayHello(false);
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value as a string.
        Object obj = resp.getResult();
        return obj == null ? null : obj.toString();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* checkHealth:                                                                 */
    /* ---------------------------------------------------------------------------- */
    public String checkHealth()
     throws TapisClientException
    {
        // Make the REST call.
        RespProbe resp = null;
        try {
            // Get the API object using default networking.
            var generalApi = new GeneralApi(_apiClient);
            resp = generalApi.checkHealth();
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value as a string.
        Object obj = resp.getResult();
        return obj == null ? null : obj.toString();
    }
    
    /* ---------------------------------------------------------------------------- */
    /* ready:                                                                       */
    /* ---------------------------------------------------------------------------- */
    public String ready()
     throws TapisClientException
    {
        // Make the REST call.
        RespProbe resp = null;
        try {
            // Get the API object using default networking.
            var generalApi = new GeneralApi(_apiClient);
            resp = generalApi.ready();
        }
        catch (ApiException e) {Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e);}
        catch (Exception e) {Utils.throwTapisClientException(-1, null, e);}
        
        // Return result value as a string.
        Object obj = resp.getResult();
        return obj == null ? null : obj.toString();
    }
    
    /* **************************************************************************** */
    /*                               Private Methods                                */
    /* **************************************************************************** */

}
