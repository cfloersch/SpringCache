package xpertss.spring.cache;

import org.springframework.http.HttpRequest;

public class SimpleCacheKeyGenerator implements CacheKeyGenerator {

   public String createKey(HttpRequest request)
   {
      return request.getMethod().name() + ":" + request.getURI();
   }
}
