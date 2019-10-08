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
import org.springframework.http.client.ClientHttpResponse;
import xpertss.cache.CacheControl;
import xpertss.cache.CacheResponse;
import xpertss.cache.CacheType;
import xpertss.cache.CachingPolicy;
import xpertss.cache.ResponseCache;
import xpertss.lang.Integers;
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
   public CacheResponse get(HttpRequest request) throws IOException
   {
      if(policy.isServableFromCache(request)) {
         CacheItem[] items = store.get(createId(request));

      }
      return null;
   }

   @Override
   public ClientHttpResponse cache(HttpRequest request, ClientHttpResponse response)
      throws IOException
   {
      if(policy.isResponseCacheable(request, response)) {
         String key = createId(request);
         CacheItem item = createCacheItem(response);
         long size = response.getHeaders().getContentLength();
         OutputStream out = store.cache(key, item, size);
         return new CachingHttpResponse(response, out);
      }
      return response;
   }


   
   public void start()
   {

   }

   public void stop()
   {

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
               .withMaxAge(getMaxAge(headers, cc.getMaxAge(type)))
               .withConditional(cc.getRevalidate(type));
      
      return item;
   }



   private int getMaxAge(HttpHeaders headers, int maxAge)
   {
      if(maxAge < 0) {
         long expires = headers.getExpires();
         if(expires < 0) {
            maxAge = 0;
         } else {
            long date = headers.getDate();
            long diff = expires - date;
            if(diff < 0) {
               maxAge = 0;
            } else {
               maxAge = Integers.safeCast(diff / 1000);
            }
         }
      }
      return maxAge;
   }

}

