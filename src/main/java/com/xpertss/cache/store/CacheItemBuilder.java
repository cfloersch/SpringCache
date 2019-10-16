/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/10/2019
 */
package com.xpertss.cache.store;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import xpertss.cache.Visibility;
import xpertss.util.Platform;

import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public final class CacheItemBuilder {

   private Path cacheFile;
   private long cached;

   private Visibility visibility;
   private int maxAge = -1;

   private String eTag;
   private long lastModified;

   private boolean conditional;
   private boolean staleOnError;



   public CacheItemBuilder setCacheFile(Path cacheFile)
   {
      this.cacheFile = cacheFile;
      return this;
   }

   public CacheItemBuilder setCached(long cached)
   {
      this.cached = cached;
      return this;
   }

   public CacheItemBuilder setVisibility(Visibility visibility)
   {
      this.visibility = visibility;
      return this;
   }

   public CacheItemBuilder setMaxAge(int maxAge)
   {
      this.maxAge = maxAge;
      return this;
   }

   public CacheItemBuilder setETag(String eTag)
   {
      this.eTag = eTag;
      return this;
   }

   public CacheItemBuilder setLastModified(long lastModified)
   {
      this.lastModified = lastModified;
      return this;
   }

   public CacheItemBuilder setConditional(boolean conditional)
   {
      this.conditional = conditional;
      return this;
   }

   public CacheItemBuilder setStaleOnError(boolean staleOnError)
   {
      this.staleOnError = staleOnError;
      return this;
   }



   public Path getCacheFile()
   {
      return (cacheFile != null) ? cacheFile : Platform.tempDir().resolve(Objects.toString(UUID.randomUUID()));
   }

   public long getCached()
   {
      return cached;
   }

   public Visibility getVisibility()
   {
      return visibility;
   }

   public int getMaxAge()
   {
      return maxAge;
   }

   public String getETag()
   {
      return eTag;
   }

   public long getLastModified()
   {
      return lastModified;
   }

   public boolean isConditional()
   {
      return conditional;
   }

   public boolean isStaleOnError()
   {
      return staleOnError;
   }


   public CacheItem build(HttpStatus status, HttpHeaders headers)
   {
      return new CacheItem(this, status, headers);
   }


   public static CacheItemBuilder create()
   {
      return new CacheItemBuilder();
   }

}
