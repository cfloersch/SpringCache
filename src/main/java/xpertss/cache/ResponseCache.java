/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package xpertss.cache;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

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
    * Get a previously cached response if one exists. The returned cache response will be
    * {@code null} if the request's response has not previously been cached or if it must
    * be verified. Otherwise, it will return a {@link ClientHttpResponse} representing the
    * cached response.
    * <p/>
    * The HttpRequest's headers may be modified by this call to force a conditional cache
    * verification by the server.
    *
    * @param request the http request to get a cache response for
    * @return a previously cached cache response or {@code null}
    * @throws IOException if an error occurs trying to fetch the cache
    */
   public ClientHttpResponse get(HttpRequest request) throws IOException;

   /**
    * This method will evaluate the request and response and return an appropriate client
    * response.
    * <p/>
    * If the response headers indicate that a previously cached copy is still VALID the
    * cache status will be updated and a previously cached client response will be
    * returned.
    * <p/>
    * If the response indicates that a previously cached response is no longer valid
    * then a CachingClientResponse will be returned that will update the cache with the
    * new content as it is downloaded from the network.
    * <p/>
    * If no previously existing cached response exists and the response is cachable
    * then a CachingClientResponse will be returned which will write the data to the
    * disk cache as it is downloaded from the network.
    * <p/>
    * If no previously existing cached response exists and the response is not cachable
    * then the returned object will the same as the supplied argument.
    *
    * @param request the request to cache the response for
    * @param response the response to cache if cachable
    * @return a caching http response proxy or the supplied http response
    * @throws IOException if an error occurs trying to create the cache
    */
   public ClientHttpResponse cache(HttpRequest request, ClientHttpResponse response)
      throws IOException;

}
