package xpertss.spring.cache;

import org.springframework.http.HttpRequest;

public interface CacheKeyGenerator {

   String createKey(HttpRequest request);
}
