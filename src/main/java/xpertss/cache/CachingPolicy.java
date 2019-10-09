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
import java.util.Date;

/**
 * Policy that determines if a request can be served from cache or a response
 * can be cached.
 */
public interface CachingPolicy {

   /**
    * Determine if the {@link ClientHttpResponse} gotten from the origin is a
    * cacheable response.
    *
    * @param request  The request that generated an origin hit.
    * @param response The response from the origin.
    * @return <tt>true</tt> if response is cacheable.
    */
   boolean isResponseCacheable(HttpRequest request, ClientHttpResponse response)
      throws IOException;

   /**
    * Determines if the given {@code HttpRequest} is allowed to be served
    * from cache.
    *
    * @param request The request to check.
    * @return <tt>true</tt> if request can be served from cache.
    */
   boolean isServableFromCache(HttpRequest request)
      throws IOException;


}
