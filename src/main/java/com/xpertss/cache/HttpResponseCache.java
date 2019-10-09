/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package com.xpertss.cache;

import com.xpertss.cache.store.CacheItem;
import com.xpertss.cache.store.CacheStore;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import xpertss.cache.CacheControl;
import xpertss.cache.CacheType;
import xpertss.cache.CachingPolicy;
import xpertss.cache.ResponseCache;
import xpertss.lang.Objects;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class HttpResponseCache implements ResponseCache {

   private final CacheStore store;
   private final CacheType type;
   private final CachingPolicy policy;

   public HttpResponseCache(CacheStore store, CacheType type)
   {
      this.store = Objects.notNull(store, "store");
      this.type = Objects.notNull(type, "type");
      this.policy = type.getPolicy();
   }

   public CacheType getType() { return type; }


   @Override
   public ClientHttpResponse get(HttpRequest request) throws IOException
   {
      if(policy.isServableFromCache(request)) {
         CacheItem[] items = store.get(createId(request));
         if(!Objects.isEmpty(items)) {
            /* TODO
             * Either mutate the request headers to add If-None-Match or If-Modified-Since
             *   or return a CachedHttpResponse
             *   or return null
             */

         }
      }
      return null;
   }

   @Override
   public ClientHttpResponse cache(HttpRequest request, ClientHttpResponse response)
      throws IOException
   {
      if(policy.isResponseCacheable(request, response)) {
         CacheItem[] items = store.get(createId(request));
         if(!Objects.isEmpty(items)) {
            if(response.getStatusCode() == HttpStatus.NOT_MODIFIED) {
               if(items.length == 1) {
                  return new CachedHttpResponse(items[0]);
               } else {

               }
            }
         }
         // TODO If response is 304 then we need to locate and return the correct cache (update access info)
         //    return CachedHttpResponse
         // TODO If request had If-Modified-Since we will need to replace the existing cache
         //    return CachingHttpResponse
         // TODO If request has If-None-Match then we need to add the new cache item
         //    return CachingHttpResponse
         // TODO Otherwise, we need to cache
         //    return CachingHttpResponse
      }
      return response;
   }


   



   private String createId(HttpRequest request)
   {
      return Objects.toString(request.getURI());
   }

   private CacheItem createCacheItem(ClientHttpResponse response)
   {
      CacheItem item = new CacheItem();

      HttpHeaders headers = response.getHeaders();
      CacheControl cc = CacheControl.valueOf(headers);

      item = item.withETag(headers.getETag())
               .withLastModified(new Date(headers.getLastModified()))
               .withMaxAge(cc.getMaxAge(type))
               .withConditional(cc.getMustRevalidate(type));
      
      return item;
   }




}

