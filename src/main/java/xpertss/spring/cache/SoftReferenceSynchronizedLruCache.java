package xpertss.spring.cache;

import java.lang.ref.SoftReference;

public class SoftReferenceSynchronizedLruCache extends SynchronizedLruCache {

   /**
    * Create a new instance with default initial capacity and load factor.
    *
    * @param name     An arbitrary name of this cache instance.
    * @param capacity The maximal capacity.
    */
   public SoftReferenceSynchronizedLruCache(String name, int capacity)
   {
      super(name, capacity);
   }

   public SoftReferenceSynchronizedLruCache(String name, int capacity, int initialCapacity, float loadFactory)
   {
      super(name, capacity, initialCapacity, loadFactory);
   }


   @Override
   public synchronized ValueWrapper get(Object key)
   {
      ValueWrapper wrapped = super.get(key);

      if(wrapped != null && wrapped.get() == null) {
         // remove entry from cache if it's not valid, perhaps removed by GC
         evict(key);
      }
      return wrapped;
   }

   @Override
   protected ValueWrapper createEntry(Object value)
   {
      return new SoftReferenceWrapper(value);
   }


   static class SoftReferenceWrapper implements ValueWrapper {

      private final SoftReference<Object> value;

      SoftReferenceWrapper(Object value)
      {
         this.value = new SoftReference<>(value);
      }

      public Object get()
      {
         return value.get();
      }
   }
}
