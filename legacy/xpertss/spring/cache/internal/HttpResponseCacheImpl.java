package xpertss.spring.cache.internal;

import xpertss.spring.cache.CacheKeyGenerator;
import xpertss.spring.cache.DefaultResponseExpirationResolver;
import xpertss.spring.cache.ResponseExpirationResolver;
import xpertss.spring.cache.SimpleCacheKeyGenerator;
import xpertss.spring.cache.internal.SizeLimitedHttpResponseReader.ResponseSizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Date;

public class HttpResponseCacheImpl implements HttpResponseCache {

   private static final Logger log = LoggerFactory.getLogger(HttpResponseCacheImpl.class);

   private final CacheKeyGenerator keyGenerator;
   private final Cache cache;


   private ResponseExpirationResolver expirationResolver;
   private HttpResponseReader responseReader;


   public HttpResponseCacheImpl(Cache cache, boolean sharedCache, int maxResponseSize)
   {
      this(cache, sharedCache, maxResponseSize, new SimpleCacheKeyGenerator());
   }

   public HttpResponseCacheImpl(Cache cache, boolean sharedCache, int maxResponseSize, CacheKeyGenerator keyGenerator)
   {
      this.cache = cache;
      this.keyGenerator = keyGenerator;
      this.expirationResolver = new DefaultResponseExpirationResolver(sharedCache);
      this.responseReader = new SizeLimitedHttpResponseReader(maxResponseSize);
   }


   public void clear()
   {
      cache.clear();
   }

   public void evict(HttpRequest request)
   {
      cache.evict(toKey(request));
   }

   public CacheEntry getCacheEntry(HttpRequest request)
   {
      ValueWrapper wrapper = cache.get(toKey(request));

      return wrapper != null ? (CacheEntry) wrapper.get() : null;
   }

   public ClientHttpResponse cacheAndReturnResponse(HttpRequest request, ClientHttpResponse response, Date requestSent, Date responseReceived)
      throws IOException
   {

      try {
         InMemoryClientHttpResponse fetchedResp = responseReader.readResponse(response);

         Date initialDate = expirationResolver.resolveInitialDate(fetchedResp, requestSent, responseReceived);
         Date expirationDate = expirationResolver.resolveExpirationDate(fetchedResp, initialDate);

         cache.put(toKey(request), new CacheEntry(fetchedResp, initialDate, expirationDate));

         return fetchedResp;

      } catch(ResponseSizeLimitExceededException ex) {
         log.info("[{} {}] {}", request.getMethod(), request.getURI(), "actual content length exceeded the limit");
         return ex.getResponse();
      }
   }


   private String toKey(HttpRequest request)
   {
      Assert.notNull(request, "request must not be null");
      return keyGenerator.createKey(request);
   }
}
