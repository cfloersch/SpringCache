package xpertss.spring.cache;

import xpertss.spring.cache.internal.CacheControl;
import xpertss.spring.cache.internal.CacheEntry;
import org.springframework.http.HttpRequest;

import java.util.Date;

import static xpertss.spring.cache.internal.CacheControl.parseCacheControl;

public class DefaultCachedEntrySuitabilityChecker implements CachedEntrySuitabilityChecker {

   public boolean canCachedEntryBeUsed(HttpRequest request, CacheEntry entry, Date now)
   {

      if(now.after(entry.getResponseExpiration())) {
         return false;
      }
      CacheControl cc = parseCacheControl(request.getHeaders());

      if(cc.getMaxAge() > -1 && responseCurrentAge(entry, now) > cc.getMaxAge()) {
         return false;
      }

      return true;
   }


   private long responseCurrentAge(CacheEntry entry, Date now)
   {
      return (now.getTime() - entry.getResponseCreated().getTime()) / 1000L;
   }
}
