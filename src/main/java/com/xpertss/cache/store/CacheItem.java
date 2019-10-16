/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package com.xpertss.cache.store;

import com.xpertss.cache.store.util.HttpUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import xpertss.cache.Visibility;
import xpertss.lang.Integers;
import xpertss.lang.Objects;
import xpertss.lang.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

/**
 * TODO convert to an abstract class or interface
 */
public class CacheItem implements Weighable {

   private final HttpHeaders headers;
   private final HttpStatus status;
   private final Path cacheFile;

   private long cached;
   private Visibility visibility;
   private int maxAge = -1;

   private boolean conditional;
   private boolean staleOnError;

   private long lastModified;
   private String eTag;



   // Technically isPublic, isPrivate, isConditional, isStale-While-Revalidate, isStale-If-Error could all be flags in a BitSet


   private CacheItem(HttpStatus status, HttpHeaders headers, Path cacheFile)
   {
      this.status = status;
      this.headers = headers;
      this.cacheFile = cacheFile;
   }

   public CacheItem(CacheItemBuilder builder, HttpStatus status, HttpHeaders headers)
   {
      this.status = Objects.notNull(status, "status");
      this.headers = HttpUtils.clone(Objects.notNull(headers, "headers"));
      this.cacheFile = builder.getCacheFile();

      this.cached = builder.getCached();
      this.visibility = builder.getVisibility();
      this.maxAge = builder.getMaxAge();
      this.conditional = builder.isConditional();
      this.staleOnError = builder.isStaleOnError();

      this.eTag = builder.getETag();
      this.lastModified = builder.getLastModified();
   }




   public HttpStatus getHttpStatus()
   {
      return status;
   }

   public HttpHeaders getHeaders()
   {
      HttpHeaders result = HttpUtils.clone(headers);
      result.set("Age", Integer.toString(getAge()));
      return result;
   }

   public Path getCacheFile()
   {
      return cacheFile;
   }





   public long getCached()
   {
      return cached;
   }


   public boolean isStale()
   {
      return System.currentTimeMillis() > getExpires();
   }

   public long getExpires()
   {
      return cached + (maxAge * 1000);
   }

   public int getAge()
   {
      return Integers.safeCast((System.currentTimeMillis() - cached) / 1000);
   }


   public boolean isConditional()
   {
      return conditional;
   }

   public boolean isStaleOnError()
   {
      return staleOnError;
   }

   public Visibility getVisibility()
   {
      return visibility;
   }





   public long getLastModified()
   {
      return lastModified;
   }
   public String getETag()
   {
      return eTag;
   }



   public boolean isETag()
   {
      return !Strings.isEmpty(eTag);
   }



   public InputStream newInput() throws IOException
   {
      return Files.newInputStream(cacheFile, READ);
   }

   public OutputStream newOutput() throws IOException
   {
      return Files.newOutputStream(cacheFile, CREATE_NEW, WRITE);
   }

   @Override
   public int weigh()
   {
      try {
         return Integers.safeCast(Files.size(cacheFile) / 1024);
      } catch(Exception e) {
         return 0;   // force the cache to reject the item
      }
   }

   public void passivate()
   {
      try {
         Files.deleteIfExists(cacheFile);
      } catch(IOException e) {
         /* TODO What to do with this */
      }
   }


}
