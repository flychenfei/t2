package com.britesnow.samplesocial.scribe.builder;

import org.scribe.builder.api.Api;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.SignatureType;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.Preconditions;

import com.britesnow.samplesocial.scribe.builder.api.DefaultApi20;
import com.britesnow.samplesocial.scribe.model.OAuthConfig;
import com.britesnow.samplesocial.scribe.model.OAuthConstants;

import java.io.OutputStream;

/**
 * Implementation of the Builder pattern, with a fluent interface that creates a
 * {@link org.scribe.oauth.OAuthService}
 *
 * @author Pablo Fernandez
 *
 */
public class ServiceBuilder
{
  private String apiKey;
  private String apiSecret;
  private String callback;
  private Api api;
  private String scope;
  private String grantType;
  private String accessType;
  private String approvalPrompt;
  private SignatureType signatureType;
  private OutputStream debugStream;

  /**
   * Default constructor
   */
  public ServiceBuilder()
  {
    this.callback = OAuthConstants.OUT_OF_BAND;
    this.signatureType = SignatureType.Header;
    this.debugStream = null;
  }

  /**
   * Configures the {@link org.scribe.builder.api.Api}
   *
   * @param apiClass the class of one of the existent {@link org.scribe.builder.api.Api}s on org.scribe.api package
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder provider(Class<? extends Api> apiClass)
  {
    this.api = createApi(apiClass);
    return this;
  }

  private Api createApi(Class<? extends Api> apiClass)
  {
    Preconditions.checkNotNull(apiClass, "Api class cannot be null");
    Api api;
    try
    {
      api = apiClass.newInstance();
    }
    catch(Exception e)
    {
      throw new OAuthException("Error while creating the Api object", e);
    }
    return api;
  }

  /**
   * Configures the {@link org.scribe.builder.api.Api}
   *
   * Overloaded version. Let's you use an instance instead of a class.
   *
   * @param api instance of {@link org.scribe.builder.api.Api}s
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder provider(Api api)
  {
	  Preconditions.checkNotNull(api, "Api cannot be null");
	  this.api = api;
	  return this;
  }

  /**
   * Adds an OAuth callback url
   *
   * @param callback callback url. Must be a valid url or 'oob' for out of band OAuth
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder callback(String callback)
  {
    Preconditions.checkNotNull(callback, "Callback can't be null");
    this.callback = callback;
    return this;
  }

  /**
   * Configures the api key
   *
   * @param apiKey The api key for your application
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder apiKey(String apiKey)
  {
    Preconditions.checkEmptyString(apiKey, "Invalid Api key");
    this.apiKey = apiKey;
    return this;
  }

  /**
   * Configures the api secret
   *
   * @param apiSecret The api secret for your application
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder apiSecret(String apiSecret)
  {
    Preconditions.checkEmptyString(apiSecret, "Invalid Api secret");
    this.apiSecret = apiSecret;
    return this;
  }

  /**
   * Configures the OAuth scope. This is only necessary in some APIs (like Google's).
   *
   * @param scope The OAuth scope
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder scope(String scope)
  {
    Preconditions.checkEmptyString(scope, "Invalid OAuth scope");
    this.scope = scope;
    return this;
  }

  /**
   * Configures the OAuth grant type.
   *
   * @param grantType
   *          The OAuth grant type
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder grantType(String grantType) {
    Preconditions.checkEmptyString(grantType, "Invalid OAuth Grant Type");
    this.grantType = grantType;
    return this;
  }

  /**
   * Configures the OAuth access type. This is only necessary in some APIs (like Google's).
   *
   * @param accessType
   *          The OAuth access type
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder accessType(String accessType) {
    Preconditions.checkEmptyString(accessType, "Invalid OAuth Access Type");
    this.accessType = accessType;
    return this;
  }

  /**
   * Configures if the user should be re-prompted for consent. This is only necessary in some APIs (like Google's).
   *
   * @param approvalPrompt
   *          The OAuth approval prompt
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder approvalPrompt(String approvalPrompt) {
    Preconditions.checkEmptyString(approvalPrompt, "Invalid OAuth Approval Prompt");
    this.approvalPrompt = approvalPrompt;
    return this;
  }

  /**
   * Configures the signature type, choose between header, querystring, etc. Defaults to Header
   *
   * @param scope The OAuth scope
   * @return the {@link ServiceBuilder} instance for method chaining
   */
  public ServiceBuilder signatureType(SignatureType type)
  {
    Preconditions.checkNotNull(type, "Signature type can't be null");
    this.signatureType = type;
    return this;
  }

  public ServiceBuilder debugStream(OutputStream stream)
  {
    Preconditions.checkNotNull(stream, "debug stream can't be null");
    this.debugStream = stream;
    return this;
  }

  public ServiceBuilder debug()
  {
    this.debugStream(System.out);
    return this;
  }

  /**
   * Returns the fully configured {@link org.scribe.oauth.OAuthService}
   *
   * @return fully configured {@link org.scribe.oauth.OAuthService}
   */
  public OAuthService build()
  {
    Preconditions.checkNotNull(api, "You must specify a valid api through the provider() method");
    Preconditions.checkEmptyString(apiKey, "You must provide an api key");
    Preconditions.checkEmptyString(apiSecret, "You must provide an api secret");
    if(api instanceof DefaultApi20){
        return ((DefaultApi20)api).createService(new OAuthConfig(apiKey, apiSecret, callback, signatureType, scope, grantType, accessType, approvalPrompt, debugStream));
    }
    return api.createService(new org.scribe.model.OAuthConfig(apiKey, apiSecret, callback, signatureType, scope, debugStream));
  }
}
