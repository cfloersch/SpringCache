/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package com.xpertss.cache.store;

import xpertss.lang.Objects;
import xpertss.time.Dates;

import java.nio.ByteBuffer;
import java.util.Date;

// TODO Add no transform, stale-on-error, etc

// TODO Add headers?
public class CacheItem {

   private Date cached, accessed = new Date();
   private Date expires;

   private String eTag;
   private Date lastModifiedDate;

   private boolean conditional;
   private ByteBuffer entity;


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


   public Date getAccessed()
   {
      return accessed;
   }

   public CacheItem withAccessed(Date accessed)
   {
      CacheItem copy = copy();
      copy.accessed = Dates.gte(accessed, new Date(), "accessed");
      return copy;
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


   public boolean isExpired()
   {
      return expires.after(new Date());
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



   // TODO I need to figure out how to handle the entity.

   public ByteBuffer getEntity()
   {
      return entity;
   }

   public CacheItem withEntity(ByteBuffer entity)
   {
      CacheItem copy = copy();
      copy.entity = Objects.notNull(entity, "entity");
      return copy;
   }
   



   private CacheItem copy()
   {
      CacheItem copy = new CacheItem();
      copy.accessed = accessed;
      copy.cached = cached;
      copy.expires = expires;
      copy.conditional = conditional;
      copy.eTag = eTag;
      copy.lastModifiedDate = lastModifiedDate;
      copy.entity = entity;
      return copy;
   }
}
