/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package xpertss.cache;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;


/**
 * A private caching policy will allow items marked as private to be cached and
 * will evaluate must-revalidate and max-age as opposed to s-max-age and
 * proxy-revalidate cache control directives.
 */
public class PrivateCachingPolicy extends AbstractCachingPolicy {

   public PrivateCachingPolicy()
   {
      super(CacheType.Private);
   }

   @Override
   public boolean isResponseCacheable(HttpRequest request, ClientHttpResponse response)
      throws IOException
   {
      HttpHeaders reqHeaders = request.getHeaders();

      if(!isCacheableMethod(request.getMethod())) return false;

      CacheControl reqCc = CacheControl.valueOf(reqHeaders.getCacheControl());
      if(reqCc.isNoStore()) return false;

      return isResponseCacheable(response);
   }




}
