/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/11/2019
 */
package com.xpertss.cache;

import com.xpertss.cache.store.CacheItem;

public interface CacheSet {

   /**
    * Returns all items that are currently considered expired based on
    * the supplied current time
    */
   public CacheItem[] getExpired(long now);

   /**
    * Adds the specified item to the set returning any items that are
    * being removed from the set as a result of the add so that they
    * may be evicted from the cache.
    */
   public CacheItem[] add(CacheItem item);


   /**
    * Return the items in the set as an array
    */
   public CacheItem[] get();


   /**
    * Return a particular item in the cache set based on the given key.
    * <p/>
    * This will return null if the set does not support key based access
    * or if the specified key could not be matched to an item.
    */
   public CacheItem get(String key);
}
