package xpertss.spring.cache;

import xpertss.spring.cache.internal.HttpResponseCache;
import xpertss.spring.cache.internal.HttpResponseCacheImpl;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * A builder for {@link CachingHttpRequestInterceptor}.
 */
public final class CachingHttpRequestInterceptorBuilder {

   private final CachingHttpRequestInterceptorBuilder parent = this;

   /**
    * Whether to behave as a shared cache (true) or a non-shared/private cache
    * (false). The default is shared cache.
    * <p>
    * <p>A private cache will not, for example, cache responses to requests
    * with Authorization headers or responses marked with <tt>Cache-Control:
    * private</tt>. If, however, the cache is only going to be used by one
    * logical "user" (behaving similarly to a browser cache), then you will
    * want to turn off the shared cache setting.</p>
    */
   private boolean sharedCache = true;

   /**
    * Specify the maximal response size in bytes to store in the cache,
    * i.e. don't cache responses bigger then this size. The default is 128 kB.
    */
   private int maxResponseSize = 128 * 1024;

   /**
    * Specify {@code CachingPolicy} to use. The default is
    * {@link DefaultCachingPolicy}.
    */
   private CachingPolicy cachingPolicy;

   /**
    * Specify the cache backend to use.
    * This cannot be used along with {@link #inMemoryCache()}.
    */
   private Cache cache;


   /**
    * Use and configure the default in-memory cache.
    * This cannot be used along with {@link #cache(Cache)}.
    */
   public InMemoryCacheBuilder inMemoryCache()
   {
      Assert.isNull(cache, "You cannot use both custom cache and built-in inMemoryCache");
      return new InMemoryCacheBuilder();
   }

   /**
    * Build and return configured {@link CachingHttpRequestInterceptor}.
    */
   public CachingHttpRequestInterceptor build()
   {
      if(cachingPolicy == null) {
         cachingPolicy = new DefaultCachingPolicy(sharedCache, maxResponseSize);
      }
      Assert.notNull(cache, "You must specify cache backend, or use inMemoryCache ");

      HttpResponseCache responseCache = new HttpResponseCacheImpl(cache, sharedCache, maxResponseSize);

      return new CachingHttpRequestInterceptor(responseCache, cachingPolicy, new DefaultCachedEntrySuitabilityChecker());
   }

   /**
    * Build a configured {@link CachingHttpRequestInterceptor} and add it to
    * the given RestTemplate.
    */
   public void enhance(RestTemplate restTemplate)
   {
      restTemplate.getInterceptors().add(build());
   }


   public final class InMemoryCacheBuilder {

      /**
       * Specify capacity of the in-memory cache, i.e. how many responses to
       * keep. The default is 64.
       */
      private int capacity = 64;

      /**
       * Whether to use {@linkplain java.lang.ref.SoftReference soft references}
       * to store responses, or not. The default is <tt>true</tt>.
       */
      private boolean softReferences = true;


      /**
       * Build and return configured {@link CachingHttpRequestInterceptor}.
       */
      public CachingHttpRequestInterceptor build()
      {
         parent.cache = softReferences ? new SoftReferenceSynchronizedLruCache("http-cache", capacity) : new SynchronizedLruCache("http-cache", capacity);
         return parent.build();
      }

      /**
       * Build a configured {@link CachingHttpRequestInterceptor} and add it to
       * the given RestTemplate.
       */
      public void enhance(RestTemplate restTemplate)
      {
         restTemplate.getInterceptors().add(build());
      }
   }
}
