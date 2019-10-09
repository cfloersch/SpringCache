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


/**
 * A shared caching policy will make determinations based on the notion that this
 * cache is shared. For example it will not allow items marked as private to be
 * cached and it will use s-max-age and proxy-revalidate as opposed to max-age
 * and must-revalidate cache-control arguments.
 */
public class SharedCachingPolicy extends AbstractCachingPolicy {

   @Override
   public boolean isResponseCacheable(HttpRequest request, ClientHttpResponse response)
   {
      HttpHeaders reqHeaders = request.getHeaders();
      HttpHeaders respHeaders = response.getHeaders();

      if(!isCacheableMethod(request.getMethod())) return false;

      CacheControl reqCc = CacheControl.valueOf(reqHeaders.getCacheControl());
      if(reqCc.isNoStore()) return false;
      if(reqCc.isPrivate()) return false;

      if(reqHeaders.getFirst("Authorization") != null) {
         CacheControl respCc = CacheControl.valueOf(respHeaders);
         if(!respCc.isPublic()) return false;
      }
      return isResponseCacheable(response);
   }


   protected boolean isExplicitlyNonCacheable(CacheControl cc)
   {
      return super.isExplicitlyNonCacheable(cc) || cc.isPrivate();
   }

}
