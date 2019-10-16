/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/11/2019
 */
package com.xpertss.cache;


import xpertss.lang.Objects;

import java.util.Map;


/**
 * A notification of the removal of a single entry. The key and/or value may be null if they were
 * already garbage collected.
 *
 * <p>Like other {@code Map.Entry} instances associated with {@code CacheBuilder}, this class holds
 * strong references to the key and value, regardless of the type of references the cache may be
 * using.
 *
 * @author Charles Fry
 * @since 10.0
 */
public final class RemovalNotification<K, V> implements Map.Entry<K, V> {
   private final K key;
   private final V value;
   private final RemovalCause cause;

   /**
    * Creates a new {@code RemovalNotification} for the given {@code key}/{@code value} pair, with
    * the given {@code cause} for the removal. The {@code key} and/or {@code value} may be
    * {@code null} if they were already garbage collected.
    */
   public static <K, V> RemovalNotification<K, V> create(K key, V value, RemovalCause cause)
   {
      return new RemovalNotification(key, value, cause);
   }

   private RemovalNotification(K key, V value, RemovalCause cause)
   {
      this.key = key;
      this.value = value;
      this.cause = Objects.notNull(cause, "cause");
   }

   /**
    * Returns the cause for which the entry was removed.
    */
   public RemovalCause getCause()
   {
      return cause;
   }

   /**
    * Returns {@code true} if there was an automatic removal due to eviction (the cause is neither
    * {@link RemovalCause#EXPLICIT} nor {@link RemovalCause#REPLACED}).
    */
   public boolean wasEvicted()
   {
      return cause.wasEvicted();
   }

   @Override
   public K getKey()
   {
      return key;
   }

   @Override
   public V getValue()
   {
      return value;
   }

   @Override
   public final V setValue(V value)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean equals(Object object)
   {
      if (object instanceof Map.Entry) {
         Map.Entry<?, ?> that = (Map.Entry<?, ?>) object;
         return Objects.equal(this.getKey(), that.getKey())
            && Objects.equal(this.getValue(), that.getValue());
      }
      return false;
   }

   @Override
   public int hashCode()
   {
      K k = getKey();
      V v = getValue();
      return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
   }

   /**
    * Returns a string representation of the form <code>{key}={value}</code>.
    */
   @Override
   public String toString()
   {
      return getKey() + "=" + getValue();
   }
}
