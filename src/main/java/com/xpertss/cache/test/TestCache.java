/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/10/2019
 */
package com.xpertss.cache.test;

import com.google.common.cache.Cache;
import com.xpertss.cache.store.CacheItem;
import xpertss.lang.Objects;

import java.net.URI;

public class TestCache {

   private final Cache<URI, CacheContainer> cache;

   public TestCache(Cache<URI, CacheContainer> cache)
   {
      this.cache = cache;
   }

   public CacheItem[] get(URI key)
   {
      CacheContainer container = cache.getIfPresent(key);
      return (container != null) ? container.getItems() : null;
   }

   public void put(URI key, CacheItem item)
   {
      // So this is not going to work
      Objects.notNull(key, "key");
      Objects.notNull(item, "item");
      cache.asMap().compute(key, (uri, cacheContainer) -> {
         if(!item.isETag()) {
            if(cacheContainer != null)
               passivate(cacheContainer.getItems());
            return new LastModifiedContainer(item);
         } else if(cacheContainer instanceof ETagContainer) {
            cacheContainer.add(item);
            return cacheContainer;
         } else if(cacheContainer != null) {
            passivate(cacheContainer.getItems());
         }
         return new ETagContainer(item);
      });
   }

   private void passivate(CacheItem[] items)
   {
      for(CacheItem item : items) passivate(item);
   }

   private void passivate(CacheItem item)
   {
      item.passivate();
   }

}
