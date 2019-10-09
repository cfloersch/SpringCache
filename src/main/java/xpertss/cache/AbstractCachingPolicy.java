/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package xpertss.cache;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import xpertss.lang.Objects;
import xpertss.lang.Strings;
import xpertss.util.Sets;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

public abstract class AbstractCachingPolicy implements CachingPolicy {

   /**
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.4">HTTP/1.1 section 13.4</a>
    */
   private static final Set<Integer> CACHEABLE_STATUSES = Sets.of(200, 203, 300, 301, 410);

   private static final Set<Integer> UNCACHEABLE_STATUSES = Sets.of(206, 303);

   private static final Set<HttpMethod> CACHEABLE_METHODS = EnumSet.of(HttpMethod.GET);

   private final CacheType type;

   protected AbstractCachingPolicy(CacheType type)
   {
      this.type = Objects.notNull(type, "type");
   }


   @Override
   public boolean isServableFromCache(HttpRequest request)
      throws IOException
   {
      if(!isCacheableMethod(request.getMethod())) return false;

      CacheControl cc = CacheControl.valueOf(request.getHeaders().getCacheControl());
      if(cc.isNoStore()) return false;
      if(cc.isNoCache()) return false;

      return true;
   }


   protected boolean isResponseCacheable(ClientHttpResponse response)
      throws IOException
   {

      boolean cacheable = false;
      HttpHeaders headers = response.getHeaders();

      int status = response.getRawStatusCode();
      if(isImplicitlyCacheableStatus(status)) {
         cacheable = true;  //MAY be cached
      } else if(isUncacheableStatus(status)) {
         return false;
      }

      CacheControl cc = CacheControl.valueOf(headers);
      if(isExplicitlyNonCacheable(cc)) {
         return false;
      }

      try {
         if(headers.getDate() < 0) {
            return false;
         }
      } catch(IllegalArgumentException ex) {
         // TODO Does this actually ever happen?
         return false;
      }

      if(!isVerifiable(headers)) {
         // Uncacheable if no verifiers present and cache is already stale
         if(cc.getMaxAge(type) < 0) return false;
      }


      // TODO Do we want to do this?? It is primarily because our cache limit is based on size
      // and we would not know the size until we had downloaded the content which is async to
      // the cache record creation which needs to know this
      if(headers.getContentLength() < 0) {
         return false;
      }



      return cacheable;
   }


   protected boolean isVerifiable(HttpHeaders headers)
   {
      return headers.getLastModified() >= 0
               || !Strings.isEmpty(headers.getETag());
   }

   protected boolean isCacheableMethod(HttpMethod method)
   {
      return CACHEABLE_METHODS.contains(method);
   }

   /**
    * Whether the given status code can be cached implicitly, i.e. even when
    * no cache header is specified.
    *
    * @param status HTTP status code
    */
   protected boolean isImplicitlyCacheableStatus(int status)
   {
      return CACHEABLE_STATUSES.contains(status);
   }

   /**
    * Whether the given status code must not be cached, even when any cache
    * header is specified.
    *
    * @param status HTTP status code
    */
   protected boolean isUncacheableStatus(int status)
   {
      return UNCACHEABLE_STATUSES.contains(status) || isUnknownStatus(status);
   }

   /**
    * Whether the given status code is considered to unknown and thus must not
    * be cached.
    * <p>
    * <i>The unknown statuses list is based on Apache HTTP Components.</i>
    *
    * @param status HTTP status code
    */
   protected boolean isUnknownStatus(int status)
   {
      return !(status >= 100 && status <= 101 || status >= 200 && status <= 206 || status >= 300 && status <= 307 || status >= 400 && status <= 417 || status >= 500 && status <= 505);
   }



   /**
    * Whether the given response must not be cached.
    */
   protected boolean isExplicitlyNonCacheable(CacheControl cc)
   {
      return cc.isNoStore();
   }


}
