package xpertss.spring.cache;

import xpertss.spring.cache.internal.CacheEntry;
import org.springframework.http.HttpRequest;

import java.util.Date;

/**
 * Determines whether a given {@link CacheEntry} is suitable to be
 * used as a response for a given {@link HttpRequest}.
 */
public interface CachedEntrySuitabilityChecker {

   boolean canCachedEntryBeUsed(HttpRequest request, CacheEntry entry, Date now);
}
