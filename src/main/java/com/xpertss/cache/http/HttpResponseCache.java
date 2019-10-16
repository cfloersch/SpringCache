/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package com.xpertss.cache.http;

import com.xpertss.cache.store.CacheItem;
import com.xpertss.cache.store.CacheStore;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import xpertss.cache.CacheType;
import xpertss.cache.CachingPolicy;
import xpertss.cache.ResponseCache;
import xpertss.lang.Objects;

import java.io.IOException;


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
         // TODO If request was conditional
         // TODO   If response is 304 then we need to locate and return the correct cache
         //          update access info
         //          update cache time
         //          return CachedHttpResponse
         // TODO   Else If response is 200 then we need to replace the cache
         // TODO     If request had If-Modified-Since
         //            return CachingHttpResponse
         // TODO     Else If request has If-None-Match then we need to add the new cache item
         //            return CachingHttpResponse
         // TODO   Else If response was 500, 502, 503, or 504 possibly return stale cache
         //          return CachedHttpResponse
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
      return null;
   }




}

