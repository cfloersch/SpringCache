package xpertss.spring.cache;

import xpertss.spring.cache.internal.CacheEntry;
import xpertss.spring.cache.internal.HttpResponseCache;
import xpertss.spring.cache.internal.HttpResponseCacheImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Date;

public class CachingHttpRequestInterceptor implements ClientHttpRequestInterceptor {

   private static final Logger log = LoggerFactory.getLogger(CachingHttpRequestInterceptor.class);

   /**
    * The cache implementation used for caching.
    */
   private final HttpResponseCache cache;

   private CachingPolicy cachingPolicy;

   private CachedEntrySuitabilityChecker cachedChecker;


   public CachingHttpRequestInterceptor(Cache cache, boolean sharedCache, int maxResponseSize)
   {
      this.cache = new HttpResponseCacheImpl(cache, sharedCache, maxResponseSize);
      this.cachingPolicy = new DefaultCachingPolicy(sharedCache, maxResponseSize);
      this.cachedChecker = new DefaultCachedEntrySuitabilityChecker();
   }

   public CachingHttpRequestInterceptor(HttpResponseCache cache, CachingPolicy cachingPolicy, CachedEntrySuitabilityChecker cachedChecker)
   {
      this.cache = cache;
      this.cachingPolicy = cachingPolicy;
      this.cachedChecker = cachedChecker;
   }


   public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException
   {

      if(!cachingPolicy.isServableFromCache(request)) {
         log("not servable from cache", request);
         return execute(request, body, execution);
      }

      CacheEntry entry = cache.getCacheEntry(request);
      if(entry == null || !cachedChecker.canCachedEntryBeUsed(request, entry, currentDate())) {
         log("cache miss", request);
         return execute(request, body, execution);

      } else {
         log("cache hit", request);
         return createResponse(entry);
      }
   }


   protected ClientHttpResponse execute(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException
   {

      final Date requestDate = currentDate();

      ClientHttpResponse response = execution.execute(request, body);

      if(cachingPolicy.isResponseCacheable(request, response)) {
         log("caching response", request);
         return cache.cacheAndReturnResponse(request, response, requestDate, currentDate());

      } else {
         log("response is not cacheable", request);
         return response;
      }
   }

   protected ClientHttpResponse createResponse(CacheEntry entry)
   {
      ClientHttpResponse response = entry.getResponse();
      Long age = (currentDate().getTime() - entry.getResponseCreated().getTime()) / 1000L;
      response.getHeaders().set("Age", age.toString());
      return response;
   }


   private void log(String message, HttpRequest request)
   {
      log.debug("[{} {}] {}", request.getMethod(), request.getURI(), message);
   }

   private Date currentDate()
   {
      return new Date();
   }
}
