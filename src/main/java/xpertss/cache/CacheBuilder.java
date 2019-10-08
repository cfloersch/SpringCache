/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package xpertss.cache;

import xpertss.lang.Numbers;
import xpertss.lang.Objects;
import xpertss.util.Platform;

import java.nio.file.Files;
import java.nio.file.Path;

public final class CacheBuilder {

   private CacheType type = CacheType.Private;

   private long cacheSize = 1024L * 1024L * 128L;           // 128 MB
   private long memorySize = 1024L * 1024L * 2L;            // 2 MB
   private long maxMemorySize = 1024L * 128;                // 128 kb
   private int maxItems = 1000;

   private Path diskStore;

   /**
    * This is the size of the cache measured in bytes. It represents the amount of
    * disk space used to cache items. Once this space has been consumed items will
    * be evicted from the cache beginning with the least recently used items.
    * <p/>
    * This defaults to 128 MB
    *
    * @param bytes the total size in bytes of the cache
    * @return this cache builder
    * @throws IllegalArgumentException if bytes is less than zero
    */
   public CacheBuilder cacheSize(long bytes)
   {
      this.cacheSize = Numbers.gte(0L, bytes, "bytes");;
      return this;
   }

   /**
    * This is the size of the memory cache. The most recently cached items will
    * be kept in memory as well as disk allowing faster access to the cached
    * item. However, once the total number of items in memory exceed this value
    * the least recently used cache item will be flushed from memory allowing
    * access only from disk.
    * <p/>
    * This defaults to 2 MB
    *
    * @param bytes the size of the memory cache in bytes
    * @return this cache builder
    * @throws IllegalArgumentException if bytes is less than zero
    */
   public CacheBuilder memorySize(long bytes)
   {
      this.memorySize = Numbers.gte(0L, bytes, "bytes");
      return this;
   }

   /**
    * This represents the maximum size of a cache item that will be retained
    * in the memory cache in bytes. Item's larger than this will be stored on
    * disk only.
    * <p/>
    * This defaults to 128 KB
    *
    * @param bytes the maximum size of a cache item to permit memory caching
    * @return this cache builder
    * @throws IllegalArgumentException if bytes is less than zero
    */
   public CacheBuilder maxMemorySize(long bytes)
   {
      this.maxMemorySize = Numbers.gte(0L, bytes, "bytes");;
      return this;
   }

   /**
    * This sets the maximum number of cache items this cache will permit.
    * Once this number is reached items will be evicted beginning with the
    * least recently used items.
    * <p/>
    * This defaults to 1000.
    *
    * @param count the count of items to support
    * @return this cache builder
    * @throws IllegalArgumentException if count is less than one
    */
   public CacheBuilder maxItems(int count)
   {
      this.maxItems = Numbers.gt(0, count, "count");
      return this;
   }


   /**
    * Specify where the disk cache should be created. Setting this to
    * {@code null} will revert to the default location.

    * @param dir the directory to create the cache files in
    * @return this cache builder
    * @throws IllegalArgumentException if the specified directory does not
    *    exist or is not a directory.
    */
   public CacheBuilder diskStore(Path dir)
   {
      if(dir != null && !Files.isDirectory(dir))
         throw new IllegalArgumentException(String.format("%s is not a directory that exists", dir));
      this.diskStore = dir;
      return this;
   }


   /**
    * Specify the this cache's type as either shared or private. A cache's
    * type will dictate how it handles various cache items as some may not
    * be cachable in a shared cache.
    * <p/>
    * This defaults to {@link CacheType#Private}
    *
    * @param type the type of this cache
    * @return this cache builder
    * @throws NullPointerException if the supplied type is null
    */
   public CacheBuilder type(CacheType type)
   {
      this.type = Objects.notNull(type, "type");
      return this;
   }


   /**
    * Build and return a CacheStore using the previously defined properties.
    *
    * @return a CacheStore instance
    * @throws IllegalArgumentException if any of the properties passed to the
    *    cache store are invalid
    */
   public CacheStore build()
   {
      CacheParams params = new CacheParams(cacheSize, memorySize, maxMemorySize, maxItems);
      Path path = Objects.ifNull(diskStore, Platform.tempDir().resolve("cache"));
      return new CacheStore(path, type, params);
   }

}
