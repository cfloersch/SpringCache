/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/10/2019
 */
package com.xpertss.cache.test;

import com.xpertss.cache.store.CacheItem;
import xpertss.cache.Visibility;
import xpertss.util.Iterables;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.LongAdder;

public class ETagContainer implements CacheContainer {

   private CacheItem publicItem;
   private Set<CacheItem> items = new TreeSet<CacheItem>(Comparator.comparing(CacheItem::getETag));

   public ETagContainer(CacheItem item)
   {
      add(item);
   }

   public void add(CacheItem item)
   {
      if(!item.isETag()) throw new IllegalArgumentException();
      if(item.getVisibility() == Visibility.Public) {
         publicItem = item;
      } else {
         items.add(item);
      }
   }


   @Override
   public CacheItem[] getItems()
   {
      CacheItem[] result = new CacheItem[items.size() + 1];
      result = items.toArray(result);
      result[result.length - 1] = publicItem;
      return result;
   }

   @Override
   public int getWeight()
   {
      LongAdder size = new LongAdder();
      Iterables.forEach(items, cacheItem -> size.add(cacheItem.weigh()));
      if(publicItem != null) size.add(publicItem.weigh());
      return size.intValue();
   }
}
