/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/11/2019
 */
package com.xpertss.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public enum RemovalCause {

   /**
    * The entry was manually removed by the user. This can result from the user invoking
    * {@link com.google.common.cache.Cache#invalidate}, {@link com.google.common.cache.Cache#invalidateAll(Iterable)}, {@link com.google.common.cache.Cache#invalidateAll()},
    * {@link Map#remove}, {@link ConcurrentMap#remove}, or {@link Iterator#remove}.
    */
   EXPLICIT {
      @Override
      boolean wasEvicted() {
         return false;
      }
   },

   /**
    * The entry itself was not actually removed, but its value was replaced by the user. This can
    * result from the user invoking {@link Cache#put}.
    */
   REPLACED {
      @Override
      boolean wasEvicted() {
         return false;
      }
   },

   /**
    * The entry was removed automatically because its key or value was garbage-collected. This
    * can occur when using {@link com.google.common.cache.CacheBuilder#weakKeys}, {@link com.google.common.cache.CacheBuilder#weakValues}, or
    * {@link com.google.common.cache.CacheBuilder#softValues}.
    */
   COLLECTED {
      @Override
      boolean wasEvicted() {
         return true;
      }
   },

   /**
    * The entry's expiration timestamp has passed. This can occur when using
    * {@link com.google.common.cache.CacheBuilder#expireAfterWrite} or {@link com.google.common.cache.CacheBuilder#expireAfterAccess}.
    */
   EXPIRED {
      @Override
      boolean wasEvicted() {
         return true;
      }
   },

   /**
    * The entry was evicted due to size constraints. This can occur when using
    * {@link com.google.common.cache.CacheBuilder#maximumSize} or {@link CacheBuilder#maximumWeight}.
    */
   SIZE {
      @Override
      boolean wasEvicted() {
         return true;
      }
   };

   /**
    * Returns {@code true} if there was an automatic removal due to eviction (the cause is neither
    * {@link #EXPLICIT} nor {@link #REPLACED}).
    */
   abstract boolean wasEvicted();

}
