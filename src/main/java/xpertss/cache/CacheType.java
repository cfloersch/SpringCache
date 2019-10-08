/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package xpertss.cache;

public enum CacheType {

   Shared(new SharedCachingPolicy()),
   Private(new PrivateCachingPolicy());


   private final CachingPolicy policy;

   private CacheType(CachingPolicy policy)
   {
      this.policy = policy;
   }

   public CachingPolicy getPolicy()
   {
      return policy;
   }
}
