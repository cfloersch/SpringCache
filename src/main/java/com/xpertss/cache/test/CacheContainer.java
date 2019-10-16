/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/10/2019
 */
package com.xpertss.cache.test;

import com.xpertss.cache.store.CacheItem;

public interface CacheContainer {

   public void add(CacheItem item);

   public CacheItem[] getItems();

   public int getWeight();

}
