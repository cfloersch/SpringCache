/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package xpertss.cache.spring;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import xpertss.cache.CacheResponse;
import xpertss.cache.ResponseCache;
import xpertss.lang.Objects;

import java.io.IOException;

public class CachingHttpInterceptor implements ClientHttpRequestInterceptor {

   private final ResponseCache cache;

   public CachingHttpInterceptor(ResponseCache cache)
   {
      this.cache = Objects.notNull(cache, "cache");
   }

   @Override
   public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution)
      throws IOException
   {
      CacheResponse cr = cache.get(request);
      if(cr != null && !cr.isConditional()) return cr.cachedResponse();
      ClientHttpResponse response = execution.execute(request, bytes);
      if(cr != null && cr.isConditional() && isNotModified(response.getStatusCode()))
         return cr.cachedResponse();   // TODO May need to supply correct eTag here
      return cache.cache(request, response);
   }

   private boolean isNotModified(HttpStatus status)
   {
      return status == HttpStatus.NOT_MODIFIED;
   }

}
