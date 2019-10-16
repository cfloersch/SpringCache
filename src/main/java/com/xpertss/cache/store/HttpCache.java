/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/10/2019
 */
package com.xpertss.cache.store;


import java.net.URI;

public interface HttpCache {


   public CacheItem[] get(URI key);

   public void put(URI key, CacheItem item);


}
