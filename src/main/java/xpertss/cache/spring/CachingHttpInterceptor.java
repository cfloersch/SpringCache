/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package xpertss.cache.spring;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
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

      ClientHttpResponse cachedResponse = cache.get(request);
      if(cachedResponse != null) return cachedResponse;
      ClientHttpResponse response = execution.execute(request, bytes);
      return cache.cache(request, response);
   }


}
