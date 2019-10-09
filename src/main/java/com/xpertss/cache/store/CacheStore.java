/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package com.xpertss.cache.store;

import xpertss.cache.CacheParams;
import xpertss.lang.Objects;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.DELETE_ON_CLOSE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * I would really like this to act like a SortedConcurrentMap where the entries
 * with the least recently used first in the set.
 * <p/>
 * The values should act like a multimap where the value is a custom set. It
 * is either a SingletonSet where any addition simply overwrites the existing
 * items in the set. Alternatively, it is a SortedCustomSet where the items
 * are sorted based on their staleness where oldest items are first. Also this
 * set should ensure only one PUBLIC item overwriting that item with any new
 * PUBLIC item and as many non-public items as desired.
 * <p/>
 * Both set implementations need to provide a quick and easy way to query the
 * weight of the items within.
 * <p/>
 * Ideally, all of this is optimized for concurrent access.
 */
public class CacheStore {

   private final Path path;
   private final CacheParams params;

   public CacheStore(Path path, CacheParams params)
   {
      this.path = Objects.notNull(path, "path");
      this.params = Objects.notNull(params, "params");
   }

   public Path getPath()
   {
      return path;
   }

   public CacheParams getParams()
   {
      return params;
   }









   public CacheItem[] get(String key) throws IOException
   {
      return null;
   }

   public void put(String key, CacheItem item) throws IOException
   {
      // if item is last_updated then we replace any existing cached items (evicting the existing ones)
      // else
      // if item is public replace any existing public item (evicting it)
      // else add to end of multimap set. May need to overwrite (and evict) any existing item with the same etag

      /*
         the last scenario where the etags are the same will only happen when the request headers prevent the item
         from being pulled from cache. In that scenario we might make a non-conditional request, get a response
         that is cachable and attempt to cache it. This item is likely newer (but also unchanged). However, we will
         update the cache freshness.
       */
   }






   // Remove and passivate all cache items associated with the given key
   public void evict(String key)
   {
   }

   // TODO Do I need an evictAll(String ... keys)

   public void evictAll()
   {
      // evict all
   }



   public int keys()
   {
      return 0; // number of keys
   }

   public int items()
   {
      return 0;   // number of cache items
   }

   public long size()
   {
      return 0L;  // disk space used
   }





   public void clean()
   {
      // perform whatever background tasks need to be performed. (if any)
   }



}