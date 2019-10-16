/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/11/2019
 */
package com.xpertss.cache;



import xpertss.lang.Numbers;
import xpertss.lang.Objects;
import xpertss.time.TimeProvider;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.lang.String.*;

public final class CacheBuilder<K,V> {

   private static final int DEFAULT_INITIAL_CAPACITY = 16;
   private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
   private static final int DEFAULT_WEIGHT = 1024 * 1024 * 128;

   static final int UNSET_INT = -1;

   int initialCapacity = UNSET_INT;
   int concurrencyLevel = UNSET_INT;
   long maximumWeight = UNSET_INT;

   long expireAfterStaleMillis = UNSET_INT;

   CacheListener<? super K, ? super V> cacheListener;
   TimeProvider timer;


   CacheBuilder() {}

   /**
    * Constructs a new {@code CacheBuilder} instance with default settings, including strong keys,
    * strong values, and no automatic eviction of any kind.
    */
   public static CacheBuilder<Object, Object> newBuilder()
   {
      return new CacheBuilder<Object, Object>();
   }


   /**
    * Sets the minimum total size for the internal hash tables. For example, if the initial capacity
    * is {@code 60}, and the concurrency level is {@code 8}, then eight segments are created, each
    * having a hash table of size eight. Providing a large enough estimate at construction time
    * avoids the need for expensive resizing operations later, but setting this value unnecessarily
    * high wastes memory.
    *
    * @throws IllegalArgumentException if {@code initialCapacity} is negative
    * @throws IllegalStateException if an initial capacity was already set
    */
   public CacheBuilder<K, V> initialCapacity(int initialCapacity)
   {
      if(this.initialCapacity != UNSET_INT)
         throw new IllegalStateException(format("initial capacity was already set to %s", this.initialCapacity));
      this.initialCapacity = Numbers.gte(0, initialCapacity, "initialCapacity");
      return this;
   }

   int getInitialCapacity()
   {
      return (initialCapacity == UNSET_INT) ? DEFAULT_INITIAL_CAPACITY : initialCapacity;
   }


   /**
    * Guides the allowed concurrency among update operations. Used as a hint for internal sizing. The
    * table is internally partitioned to try to permit the indicated number of concurrent updates
    * without contention. Because assignment of entries to these partitions is not necessarily
    * uniform, the actual concurrency observed may vary. Ideally, you should choose a value to
    * accommodate as many threads as will ever concurrently modify the table. Using a significantly
    * higher value than you need can waste space and time, and a significantly lower value can lead
    * to thread contention. But overestimates and underestimates within an order of magnitude do not
    * usually have much noticeable impact. A value of one permits only one thread to modify the cache
    * at a time, but since read operations and cache loading computations can proceed concurrently,
    * this still yields higher concurrency than full synchronization.
    *
    * <p> Defaults to 4. <b>Note:</b>The default may change in the future. If you care about this
    * value, you should always choose it explicitly.
    *
    * <p>The current implementation uses the concurrency level to create a fixed number of hashtable
    * segments, each governed by its own write lock. The segment lock is taken once for each explicit
    * write, and twice for each cache loading computation (once prior to loading the new value,
    * and once after loading completes). Much internal cache management is performed at the segment
    * granularity. For example, access queues and write queues are kept per segment when they are
    * required by the selected eviction algorithm. As such, when writing unit tests it is not
    * uncommon to specify {@code concurrencyLevel(1)} in order to achieve more deterministic eviction
    * behavior.
    *
    * <p>Note that future implementations may abandon segment locking in favor of more advanced
    * concurrency controls.
    *
    * @throws IllegalArgumentException if {@code concurrencyLevel} is nonpositive
    * @throws IllegalStateException if a concurrency level was already set
    */
   public CacheBuilder<K, V> concurrencyLevel(int concurrencyLevel)
   {
      if(this.concurrencyLevel != UNSET_INT)
         throw new IllegalStateException(format("concurrency level was already set to %s", this.concurrencyLevel));
      this.concurrencyLevel = Numbers.gt(0, concurrencyLevel, "concurrencyLevel");
      return this;
   }

   int getConcurrencyLevel()
   {
      return (concurrencyLevel == UNSET_INT) ? DEFAULT_CONCURRENCY_LEVEL : concurrencyLevel;
   }



   /**
    * Specifies the maximum weight of entries the cache may contain. Each cache item has the ability
    * to report a weight.
    * <p/>
    * Note that the cache <b>may evict an entry before this limit is exceeded</b>. As the cache size
    * grows close to the maximum, the cache evicts entries that are less likely to be used again.
    * For example, the cache may evict an entry because it hasn't been used recently or very often.
    * <p/>
    * When {@code weight} is zero, elements will be evicted immediately after being loaded into cache.
    * This can be useful in testing, or to disable caching temporarily without a code change.
    * <p/>
    * Note that weight is only used to determine whether the cache is over capacity; it has no effect
    * on selecting which entry should be evicted next.
    *
    * @param weight the maximum total weight of entries the cache may contain
    * @throws IllegalArgumentException if {@code weight} is negative
    * @throws IllegalStateException if a maximum weight or size was already set
    */
   public CacheBuilder<K, V> maximumWeight(long weight)
   {
      if(this.maximumWeight != UNSET_INT)
         throw new IllegalStateException(format("maximum weight was already set to %s", this.maximumWeight));
      this.maximumWeight = Numbers.gte(0L, weight, "weight");
      return this;
   }

   long getMaximumWeight()
   {
      return (maximumWeight == UNSET_INT) ? DEFAULT_WEIGHT : maximumWeight;
   }




   /**
    * Specifies a nanosecond-precision time source for this cache. By default,
    * {@link System#nanoTime} is used.
    * <p/>
    * The primary intent of this method is to facilitate testing of caches with a fake or mock
    * time source.
    *
    * @throws IllegalStateException if a timer was already set
    */
   public CacheBuilder<K, V> timeProvider(TimeProvider timer)
   {
      if(this.timer != null)
         throw new IllegalStateException("time provider was already set");
      this.timer = Objects.notNull(timer, "timer");
      return this;
   }

   TimeProvider getTimeProvider()
   {
      return (timer == null) ? TimeProvider.get() : timer;
   }




   /**
    * Specifies a listener instance that caches should notify each time an entry is removed for any
    * {@linkplain RemovalCause reason}. Each cache created by this builder will invoke this listener
    * as part of the routine maintenance described in the class documentation above.
    *
    * <p><b>Warning:</b> after invoking this method, do not continue to use <i>this</i> cache
    * builder reference; instead use the reference this method <i>returns</i>. At runtime, these
    * point to the same instance, but only the returned reference has the correct generic type
    * information so as to ensure type safety. For best results, use the standard method-chaining
    * idiom illustrated in the class documentation above, configuring a builder and building your
    * cache in a single statement. Failure to heed this advice can result in a {@link
    * ClassCastException} being thrown by a cache operation at some <i>undefined</i> point in the
    * future.
    *
    * <p><b>Warning:</b> any exception thrown by {@code listener} will <i>not</i> be propagated to
    * the {@code Cache} user, only logged via a {@link Logger}.
    *
    * @return the cache builder reference that should be used instead of {@code this} for any
    *     remaining configuration and cache building
    * @throws IllegalStateException if a removal listener was already set
    */
   public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> cacheListener(CacheListener<? super K1, ? super V1> listener)
   {
      if(this.cacheListener != null)
         throw new IllegalStateException("cache listener was already set");

      // safely limiting the kinds of caches this can produce
      @SuppressWarnings("unchecked")
      CacheBuilder<K1, V1> me = (CacheBuilder<K1, V1>) this;
      me.cacheListener =  Objects.notNull(listener, "listener");
      return me;
   }

   // Make a safe contravariant cast now so we don't have to do it over and over.
   @SuppressWarnings("unchecked")
   <K1 extends K, V1 extends V> CacheListener<K1, V1> getRemovalListener()
   {
      return (CacheListener<K1, V1>) cacheListener;
   }








   /**
    * Specifies that each entry should be automatically removed from the cache once a fixed duration
    * has elapsed after the entry has become stale.
    * <p/>
    * Expired entries may be counted in {@link Cache#entries()}, but will never be visible to read or
    * write operations. Expired entries are cleaned up as part of the routine maintenance described
    * in the class javadoc.
    *
    * @param duration the length of time after an entry is considered stale that it should be
    *     automatically removed
    * @param unit the unit that {@code duration} is expressed in
    * @throws IllegalArgumentException if {@code duration} is negative
    * @throws IllegalStateException if the time was already set
    */
   public CacheBuilder<K, V> expireAfterStale(long duration, TimeUnit unit)
   {
      if(this.expireAfterStaleMillis != UNSET_INT)
         throw new IllegalStateException(format("expireAfterStale was already set to %s ms", this.expireAfterStaleMillis));
      this.expireAfterStaleMillis = unit.toMillis(Numbers.gte(0L, duration, "duration"));
      return this;
   }

   long getExpireAfterStaleMillis()
   {
      return (expireAfterStaleMillis == UNSET_INT) ? 0 : expireAfterStaleMillis;
   }





   /**
    * Builds and returns a cache with the requested features.
    * <p/>
    * This method does not alter the state of this {@code CacheBuilder} instance, so it
    * can be invoked again to create multiple independent caches.
    *
    * @return a cache having the requested features
    */
   public <K1 extends K, V1 extends V> Cache<K1, V1> build()
   {
      return new LocalCache.LocalManualCache<K1, V1>(this);
   }


   /**
    * Returns a string representation for this CacheBuilder instance. The exact form of the returned
    * string is not specified.
    */
   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder("CacheBuilder{");
      sb.append("initialCapacity=").append(initialCapacity);
      sb.append(", concurrencyLevel=").append(concurrencyLevel);
      sb.append(", maximumWeight=").append(maximumWeight);
      sb.append(", expireAfterStaleMillis=").append(expireAfterStaleMillis).append("s");
      sb.append(", cacheListener=").append(cacheListener);
      sb.append(", timer=").append(timer);
      sb.append('}');
      return sb.toString();
   }
}
