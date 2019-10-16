/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/11/2019
 */
package com.xpertss.cache;

public interface Cache<K, V> {

   /**
    * Returns the value associated with {@code key} in this cache, or {@code null} if there
    * is no cached value for {@code key}.
    */
   V get(K key);


   /**
    * Associates {@code value} with {@code key} in this cache. If the cache previously contained
    * a value associated with {@code key}, the old value is replaced by {@code value}.
    */
   void put(K key, V value);


   /**
    * Returns the approximate number of entries in this cache.
    */
   long entries();

   /**
    * Returns the approximate number of items in this cache.
    */
   long items();

   /**
    * Returns the weight of the items currently stored in the cache.
    */
   long weight();

   /**
    * Evicts all items from the cache.
    */
   void evict();

   /**
    * Evicts all items stored under the given key
    */
   void evict(K key);
   
}
