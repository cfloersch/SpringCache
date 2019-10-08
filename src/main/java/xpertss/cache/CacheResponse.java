/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package xpertss.cache;


import org.springframework.http.client.ClientHttpResponse;

/**
 * A CacheResponse is a container providing access to a previously cached
 * response and some meta data describing how it the cached response may
 * be used.
 */
public interface CacheResponse {

   /**
    * Indicates whether the caller should execute a conditional validation
    * request to determine that the cached response can be used. This will
    * be {@code true} if the previously cached response has exceeded its
    * {@code max-age} and requires revalidation, {@code false} otherwise.
    *
    * @return true if the cache needs to be revalidated
    */
   public boolean isConditional();

   /**
    * The actual cached response that may be returned to the caller to allow
    * access to the underlying cached entity and headers.
    *
    * @return a client http response representing the cached response.
    */
   public ClientHttpResponse cachedResponse();

}
