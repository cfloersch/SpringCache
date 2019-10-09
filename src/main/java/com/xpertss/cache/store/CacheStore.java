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

   // Add Multimap item
   public void put(String key, CacheItem item) throws IOException
   {
      // TODO Must be smart enough to add items only if they are not public
      // may require items to be evicted to make room
   }

   // Clear any items in the multimap and add item
   public void replace(String key, CacheItem item) throws IOException
   {
      // may require items to be evicted to make room
   }




   // Remove and passivate all cache items associated with the given key
   public void evict(String key)
   {
   }

   public void clear()
   {
      // evict all
   }

   public void clean()
   {
      // perform whatever background tasks need to be performed. (if any)
   }


   public int size()
   {
      return 0; // number of keys, items, or total disk space?
   }








}