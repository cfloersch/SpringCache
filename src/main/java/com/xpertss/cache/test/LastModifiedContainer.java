/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/10/2019
 */
package com.xpertss.cache.test;

import com.xpertss.cache.store.CacheItem;
import xpertss.lang.Objects;

public class LastModifiedContainer implements CacheContainer {

   private final CacheItem item;

   public LastModifiedContainer(CacheItem item)
   {
      this.item = Objects.notNull(item, "item");
   }

   public void add(CacheItem item)
   {
      throw new UnsupportedOperationException();
   }


   @Override
   public CacheItem[] getItems()
   {
      return Objects.toArray(item);
   }

   @Override
   public int getWeight()
   {
      return item.weigh();
   }
   
}
