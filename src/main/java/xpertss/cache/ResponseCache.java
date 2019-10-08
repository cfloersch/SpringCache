/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package xpertss.cache;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Represents an Http Response Cache.
 * <p/>
 * Implementations will provide a means to obtain items from the cache as well
 * as a means to store new responses in the cache. These implementations will
 * apply all of the traditional cache logic but leave the validation of the
 * cache to the caller.
 */
public interface ResponseCache {

   /**
    * Get a previously cached response if one exists. The returned cache response
    * will be {@code null} if the request's response has not previously been cached.
    * Otherwise, it will return a {@link CacheResponse} providing access to the cached
    * response as well as some meta-data indicating whether the cached response can
    * be accessed directly or only as part of a validation call.
    *
    * @param request the http request to get a cache response for
    * @return a previously cached cache response or {@code null}
    */
   public CacheResponse get(HttpRequest request);

   /**
    * This method will evaluate the request and response and determine if the response
    * is cachable. It will return a ClientHttpResponse in either case, however, if the
    * response is not cachable the returned object will be the same as the supplied
    * argument.
    *
    * @param request the request to cache the response for
    * @param response the response to cache if cachable
    * @return a caching http response proxy or the supplied http response
    */
   public ClientHttpResponse cache(HttpRequest request, ClientHttpResponse response);

}
