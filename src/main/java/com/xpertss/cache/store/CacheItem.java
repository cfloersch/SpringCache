/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package com.xpertss.cache.store;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import xpertss.cache.Visibility;
import xpertss.time.Dates;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import static java.nio.file.StandardOpenOption.*;

public class CacheItem {

   private Date cached = new Date();
   private Date expires;

   private String eTag;
   private Date lastModifiedDate;

   private boolean conditional;
   private boolean staleOnError;
   private Visibility visibility;

   private HttpHeaders headers;
   private HttpStatus status;

   private Path cacheFile;

   // Technically isPublic, isPrivate, isConditional, isStale-While-Revalidate, isStale-If-Error could all be flags in a BitSet

   public CacheItem()
   {
   }

   public Date getCached()
   {
      return cached;
   }

   public CacheItem withCached(Date cached)
   {
      CacheItem copy = copy();
      copy.cached = Dates.gte(cached, new Date(), "cached");
      return copy;
   }




   public boolean isExpired()
   {
      return expires.after(new Date());
   }

   public Date getExpires()
   {
      return expires;
   }

   public CacheItem withExpires(Date expires)
   {
      CacheItem copy = copy();
      copy.expires = Dates.gte(expires, new Date(), "expires");
      return copy;
   }

   public CacheItem withMaxAge(int maxAge)
   {
      CacheItem copy = copy();
      copy.expires = new Date(cached.getTime() + (maxAge * 1000L));
      return copy;
   }








   public Date getLastModifiedDate()
   {
      return lastModifiedDate;
   }

   public CacheItem withLastModified(Date lastModified)
   {
      CacheItem copy = copy();
      copy.lastModifiedDate = lastModified;
      return copy;
   }


   public String getETag()
   {
      return eTag;
   }

   public CacheItem withETag(String eTag)
   {
      CacheItem copy = copy();
      copy.eTag = eTag;
      return copy;
   }




   public boolean isConditional()
   {
      return conditional;
   }

   public CacheItem withConditional(boolean conditional)
   {
      CacheItem copy = copy();
      copy.conditional = conditional;
      return copy;
   }

   public boolean isStaleOnError()
   {
      return staleOnError;
   }

   public CacheItem withStaleOnError(boolean staleOnError)
   {
      CacheItem copy = copy();
      copy.staleOnError = staleOnError;
      return copy;
   }

   public Visibility getVisibility()
   {
      return visibility;
   }

   public CacheItem withVisibility(Visibility visibility)
   {
      CacheItem copy = copy();
      copy.visibility = visibility;
      return copy;
   }




   public HttpStatus getStatus()
   {
      return status;
   }

   public CacheItem withHttpStatus(HttpStatus status)
   {
      CacheItem copy = copy();
      copy.status = status;
      return copy;
   }


   public HttpHeaders getHeaders()
   {
      HttpHeaders result = new HttpHeaders();
      result.addAll(headers);
      return result;
   }

   public CacheItem withHeaders(HttpHeaders headers)
   {
      CacheItem copy = copy();
      copy.headers = headers;
      return copy;
   }





   public Path getCacheFile()
   {
      return cacheFile;
   }

   public CacheItem withCacheFile(Path cacheFile)
   {
      CacheItem copy = copy();
      copy.cacheFile = cacheFile;
      return copy;
   }






   public InputStream newInput() throws IOException
   {
      return Files.newInputStream(cacheFile, READ);
   }

   public OutputStream newOutput() throws IOException
   {
      return Files.newOutputStream(cacheFile, CREATE_NEW, WRITE);
   }

   public void passivate()
   {
      try {
         Files.deleteIfExists(cacheFile);
      } catch(IOException e) {
         /* TODO What to do with this */
      }
   }


   

   private CacheItem copy()
   {
      CacheItem copy = new CacheItem();
      copy.cached = cached;
      copy.expires = expires;
      copy.eTag = eTag;
      copy.lastModifiedDate = lastModifiedDate;
      copy.conditional = conditional;
      copy.staleOnError = staleOnError;
      copy.visibility = visibility;
      copy.headers = headers;
      copy.status = status;
      copy.cacheFile = cacheFile;
      return copy;
   }
}
