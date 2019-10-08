/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package xpertss.cache;

import xpertss.lang.Longs;

import java.math.RoundingMode;
import java.util.Objects;

public class CacheParams {

   private final long cacheSize;
   private final long memorySize;
   private final long maxMemorySize;
   private final int maxItems;

   public CacheParams(long cacheSize, long memorySize, long maxMemorySize, int maxItems)
   {
      this.cacheSize = cacheSize;
      this.memorySize = memorySize;
      this.maxMemorySize = maxMemorySize;
      this.maxItems = maxItems;
   }

   public long getCacheSize()
   {
      return cacheSize;
   }

   public long getMemorySize()
   {
      return memorySize;
   }

   public long getMaxMemorySize()
   {
      return maxMemorySize;
   }

   public int getMaxItems()
   {
      return maxItems;
   }





   @Override
   public boolean equals(Object o)
   {
      if(o instanceof CacheParams) {
         CacheParams that = (CacheParams) o;
         return cacheSize == that.cacheSize
                  && memorySize == that.memorySize
                  && maxMemorySize == that.maxMemorySize
                  && maxItems == that.maxItems;
      }
      return false;
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(cacheSize, memorySize, maxMemorySize, maxItems);
   }


   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder("CacheParams{");
      sb.append("cacheSize=").append(cacheSize);
      sb.append(", memorySize=").append(memorySize);
      sb.append(", maxMemorySize=").append(maxMemorySize);
      sb.append(", maxItems=").append(maxItems);
      sb.append('}');
      return sb.toString();
   }
}
