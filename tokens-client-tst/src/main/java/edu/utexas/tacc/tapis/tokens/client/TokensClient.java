package edu.utexas.tacc.tapis.tokens.client;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

import edu.utexas.tacc.tapis.client.shared.Utils;
import edu.utexas.tacc.tapis.client.shared.exceptions.TapisClientException;
import edu.utexas.tacc.tapis.client.shared.ClientTapisGsonUtils;
import edu.utexas.tacc.tapis.tokens.client.gen.ApiClient;
import edu.utexas.tacc.tapis.tokens.client.gen.ApiException;
import edu.utexas.tacc.tapis.tokens.client.gen.Configuration;
import edu.utexas.tacc.tapis.tokens.client.gen.api.TokensApi;
import edu.utexas.tacc.tapis.tokens.client.gen.model.InlineObject1;
import edu.utexas.tacc.tapis.tokens.client.gen.model.NewTokenResponse;
import edu.utexas.tacc.tapis.tokens.client.model.CreateTokenParms;
import edu.utexas.tacc.tapis.tokens.client.model.RefreshTokenParms;
import edu.utexas.tacc.tapis.tokens.client.model.TapisAccessToken;
import edu.utexas.tacc.tapis.tokens.client.model.TapisRefreshToken;
import edu.utexas.tacc.tapis.tokens.client.model.TokenResponsePackage;

/**
 * Class providing a convenient front-end for the automatically generated client code
 * for the Tokens Service REST API.
 * Underlying client classes with "gen" in the package name are generated by
 * openapi-generator each time a build is run.
 */
public class TokensClient
{
  // ************************************************************************
  // *********************** Constants **************************************
  // ************************************************************************

  // ************************************************************************
  // ************************* Enums ****************************************
  // ************************************************************************
 
  // ************************************************************************
  // *********************** Fields *****************************************
  // ************************************************************************
  // Response serializer.
  private static final Gson _gson = ClientTapisGsonUtils.getGson();

  // ************************************************************************
  // *********************** Constructors ***********************************
  // ************************************************************************

  public TokensClient() { }

  /**
   * Constructor that overrides the compiled-in basePath value in ApiClient.
   * This constructor is typically used in production.
   * <p>
   * The path includes the URL prefix up to and including the service root.  By
   * default this value is http://localhost:8080/security.  In production environments
   * the protocol is https and the host/port will be specific to that environment.
   *
   * @param path the base path
   */
  public TokensClient(String path) {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    if (!StringUtils.isBlank(path)) apiClient.setBasePath(path);
  }

  /**
   * Constructor that overrides the compiled-in basePath value in ApiClient and sets
   * basic auth user and password (if provided)
   * This constructor is typically used in production.
   * <p>
   * The path includes the URL prefix up to and including the service root.  By
   * default this value is http://localhost:8080/security.  In production environments
   * the protocol is https and the host/port will be specific to that environment.
   *
   * @param path the base path
   * @param userName basic auth username
   * @param password basic auth password
   */
  public TokensClient(String path, String userName, String password) {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    if (!StringUtils.isBlank(path)) apiClient.setBasePath(path);
    if (!StringUtils.isBlank(userName)) apiClient.setUsername(userName);
    if (!StringUtils.isBlank(password)) apiClient.setPassword(password);
  }

  // ************************************************************************
  // *********************** Public Methods *********************************
  // ************************************************************************

  /**
   * getApiClient: Return underlying ApiClient
   */
  public ApiClient getApiClient() { return Configuration.getDefaultApiClient(); }

  /**
   * addDefaultHeader: Add http header to client
   */
  public ApiClient addDefaultHeader(String key, String val)
  {
    return Configuration.getDefaultApiClient().addDefaultHeader(key, val);
  }

  /** The general token request handler that allows all possible token parameters.
   * The parameters can specify that both an access and a refresh token be returned.
   * Each returned token has associated expiration information.  The result object
   * is always non-null but the tokens it contains may be null.
   * 
   * @param parms a container object that holds all the create parameters
   * @return a non-null package that contains zero, one or two tokens
   * @throws TapisClientException on error
   */
  public TokenResponsePackage createToken(CreateTokenParms parms) 
   throws TapisClientException
  {
      // Make the call and return the result
      Map resp = null;
      try { 
          var tokApi = new TokensApi();
          resp = (Map) tokApi.createToken(parms); 
      }
      catch (ApiException e) { Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e); }
      catch (Exception e) { Utils.throwTapisClientException(-1, null, e); }
      
      // Create the result object.
      var tokenPkg = new TokenResponsePackage();
      
      // Marshal only the result from the map.
      String json = _gson.toJson(resp.get("result"));
      if (StringUtils.isBlank(json)) return tokenPkg;
      
      // Dig down to the access token string.
      var tokResp = _gson.fromJson(json, NewTokenResponse.class);
      if (tokResp == null) return tokenPkg;
      
      // Get the access token.
      var tokRespAccess = tokResp.getAccessToken();
      if (tokRespAccess != null) {
          var wrapper = new TapisAccessToken();
          wrapper.setAccessToken(tokRespAccess.getAccessToken());
          wrapper.setExpiresAt(tokRespAccess.getExpiresAt());
          wrapper.setExpiresIn(tokRespAccess.getExpiresIn());
          tokenPkg.setAccessToken(wrapper);
      }
      
      // Get the refresh token.
      var tokRespRefresh = tokResp.getRefreshToken();
      if (tokRespRefresh != null) {
          var wrapper = new TapisRefreshToken();
          wrapper.setRefreshToken(tokRespRefresh.getRefreshToken());
          wrapper.setExpiresAt(tokRespRefresh.getExpiresAt());
          wrapper.setExpiresIn(tokRespRefresh.getExpiresIn());
          tokenPkg.setRefreshToken(wrapper);
      }
      
      // Non-null but may be empty.
      return tokenPkg;
  }
  
  /** The token refresh handler that allows all possible token parameters.
   * 
   * @param parms a container object that holds the serialized refresh token
   * @return a non-null package that contains zero, one or two tokens
   * @throws TapisClientException on error
   */
  public TokenResponsePackage refreshToken(RefreshTokenParms parms) 
   throws TapisClientException
  {
      // Make the call and return the result
      Map resp = null;
      try { 
          var tokApi = new TokensApi();
          resp = (Map) tokApi.refreshToken(parms); 
      }
      catch (ApiException e) { Utils.throwTapisClientException(e.getCode(), e.getResponseBody(), e); }
      catch (Exception e) { Utils.throwTapisClientException(-1, null, e); }
      
      // Create the result object.
      var tokenPkg = new TokenResponsePackage();
      
      // Marshal only the result from the map.
      String json = _gson.toJson(resp.get("result"));
      if (StringUtils.isBlank(json)) return tokenPkg;
      
      // Dig down to the access token string.
      var tokResp = _gson.fromJson(json, NewTokenResponse.class);
      if (tokResp == null) return tokenPkg;
      
      // Get the access token.
      var tokRespAccess = tokResp.getAccessToken();
      if (tokRespAccess != null) {
          var wrapper = new TapisAccessToken();
          wrapper.setAccessToken(tokRespAccess.getAccessToken());
          wrapper.setExpiresAt(tokRespAccess.getExpiresAt());
          wrapper.setExpiresIn(tokRespAccess.getExpiresIn());
          tokenPkg.setAccessToken(wrapper);
      }
      
      // Get the refresh token.
      var tokRespRefresh = tokResp.getRefreshToken();
      if (tokRespRefresh != null) {
          var wrapper = new TapisRefreshToken();
          wrapper.setRefreshToken(tokRespRefresh.getRefreshToken());
          wrapper.setExpiresAt(tokRespRefresh.getExpiresAt());
          wrapper.setExpiresIn(tokRespRefresh.getExpiresIn());
          tokenPkg.setRefreshToken(wrapper);
      }
      
      // Non-null but may be empty.
      return tokenPkg;
  }
  
  /**
   * Convenience method to get a JWT token for a service call
   */
  public String getSvcToken(String tenant, String serviceName) throws Exception
  {
    return getToken(tenant, serviceName, InlineObject1.AccountTypeEnum.SERVICE);
  }

  /**
   * Convenience method to get a JWT token for a user
   */
  public String getUsrToken(String tenant, String userName) throws Exception
  {
    return getToken(tenant, userName, InlineObject1.AccountTypeEnum.USER);
  }
  
  /**
   * Convenience method to get a JWT token of USER or SERVICE type
   */
  private String getToken(String tenant, String name, 
                          InlineObject1.AccountTypeEnum tokType) 
   throws TapisClientException
  {
    // Build the request
    var req = new CreateTokenParms();
    req.accountType(tokType);
    req.tokenTenantId(tenant);
    req.tokenUsername(name);
    
    // Make the call and return the result.
    var tokenPkg = createToken(req);
    var accessTokenWrapper = tokenPkg.getAccessToken();
    if (accessTokenWrapper == null) return null;
      else return accessTokenWrapper.getAccessToken();
  }
  
  /* **************************************************************************** */
  /*                               Private Methods                                */
  /* **************************************************************************** */
}
