package xpertss.cache;


import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import xpertss.lang.Integers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xpertss.cache.CacheType.Shared;

/**
 * Represents a HTTP Cache-Control response header and parses it from string.
 * <p/>
 * Note: This class ignores <tt>1#field-name</tt> parameter for <tt>private</tt> and
 * <tt>no-cache</tt> directive and cache extensions.
 *
 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">HTTP/1.1 section 14.9</a>
 *
 * TODO Add stale-on-error/etc
 */
public class CacheControl {

   // copied from org.apache.abdera.protocol.util.CacheControlUtil
   private static final Pattern PATTERN = Pattern.compile("\\s*([\\w\\-]+)\\s*(=)?\\s*(\\-?\\d+|\\\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)+\\\")?\\s*");

   /**
    * Corresponds to the <tt>max-age</tt> cache control directive.
    * The default value is <tt>-1</tt>, i.e. not specified.
    *
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1 section 14.9.3</a>
    */
   private int maxAge = -1;

   /**
    * Corresponds to the <tt>s-maxage</tt> cache control directive.
    * The default value is <tt>-1</tt>, i.e. not specified.
    *
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3">HTTP/1.1 section 14.9.3</a>
    */
   private int sMaxAge = -1;

   /**
    * Whether the <tt>must-revalidate</tt> directive is specified.
    * The default value is <tt>false</tt>.
    *
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1 section 14.9.4</a>
    */
   private boolean isMustRevalidate = false;

   /**
    * Whether the <tt>no-cache</tt> directive is specified.
    * The default value is <tt>false</tt>.
    *
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
    */
   private boolean isNoCache = false;

   /**
    * Whether the <tt>no-store</tt> directive is specified.
    * The default value is <tt>false</tt>.
    *
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.2">HTTP/1.1 section 14.9.2</a>
    */
   private boolean isNoStore = false;


   /**
    * Whether the <tt>no-transform</tt> directive is specified.
    * The default value is <tt>false</tt>.
    *
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.5">HTTP/1.1 section 14.9.5</a>
    */
   private boolean isNoTransform = false;

   /**
    * Whether the <tt>private</tt> directive is specified.
    * The default value is <tt>false</tt>.
    *
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
    */
   private boolean isPrivate = false;

   /**
    * Whether the <tt>public</tt> directive is specified.
    * The default value is <tt>false</tt>.
    *
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1">HTTP/1.1 section 14.9.1</a>
    */
   private boolean isPublic = false;

   /**
    * Whether the <tt>proxy-revalidate</tt> directive is specified.
    * The default value is <tt>false</tt>.
    *
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4">HTTP/1.1 section 14.9.4</a>
    */
   private boolean isProxyRevalidate = false;



   /**
    * Whether the <tt>immutable</tt> directive is specified.
    * The default value is <tt>false</tt>.
    *
    * @see <a href="https://tools.ietf.org/html/rfc8246">HTTP Immutable Responses</a>
    */
   private boolean isImmutable = false;

   /**
    * Whether the <tt>stale-if-error</tt> directive is specified.
    * The default value is <tt>false</tt>.
    *
    * @see <a href="https://tools.ietf.org/html/rfc5861#section-4">HTTP Cache-Control Extensions for Stale Content</a>
    */
   private boolean isStaleIfError = false;

   /**
    * Whether the <tt>stale-while-revalidate</tt> directive is specified.
    * The default value is <tt>false</tt>.
    *
    * @see <a href="https://tools.ietf.org/html/rfc5861#section-3">HTTP Cache-Control Extensions for Stale Content</a>
    */
   private boolean isStaleWhileRevalidate = false;

   
   /**
    * Creates a new instance of CacheControl by parsing the Cache-Control
    * header. Additionally, if max-age or s-max-age are not specified in
    * the Cache-Control header the Expires header will be parsed instead.
    *
    * @param headers the request/response headers to pull from
    */
   public static CacheControl valueOf(HttpHeaders headers)
   {
      CacheControl cc = valueOf(headers.getCacheControl());
      if(cc.getMaxAge(Shared) < 0) {
         long expires = headers.getExpires();
         if(expires < 0) {
            cc.setMaxAge(0);
         } else {
            long date = headers.getDate();
            long diff = expires - date;
            if(diff < 0) {
               cc.setMaxAge(0);
            } else {
               cc.setMaxAge(Integers.safeCast(diff / 1000));
            }
         }
      }
      return cc;
   }

   /**
    * Creates a new instance of CacheControl by parsing the supplied string.
    *
    * @param value the Cache-Control header value as a string.
    */
   public static CacheControl valueOf(String value)
   {
      CacheControl cc = new CacheControl();

      if(value != null) {
         Matcher matcher = PATTERN.matcher(value);
         while(matcher.find()) {
            switch(matcher.group(1).toLowerCase()) {
               case "max-age":
                  cc.setMaxAge(Integer.parseInt(matcher.group(3)));
                  break;
               case "s-maxage":
                  cc.setSMaxAge(Integer.parseInt(matcher.group(3)));
                  break;
               case "must-revalidate":
                  cc.setMustRevalidate(true);
                  break;
               case "no-cache":
                  cc.setNoCache(true);
                  break;
               case "no-store":
                  cc.setNoStore(true);
                  break;
               case "no-transform":
                  cc.setNoTransform(true);
                  break;
               case "private":
                  cc.setPrivate(true);
                  break;
               case "public":
                  cc.setPublic(true);
                  break;
               case "proxy-revalidate":
                  cc.setProxyRevalidate(true);
                  break;
               case "immutable":
                  cc.setImmutable(true);
                  break;
               case "stale-while-revalidate":
                  cc.setStaleWhileRevalidate(true);
                  break;
               case "stale-if-error":
                  cc.setStaleIfError(true);
                  break;
               default: //ignore
            }
         }
      }
      return cc;
   }

   public static CacheControl parseCacheControl(HttpHeaders headers)
   {
      Assert.notNull(headers, "headers must not be null");

      return valueOf(headers.getCacheControl());
   }


   public int getMaxAge()
   {
      return maxAge;
   }

   public void setMaxAge(int maxAge)
   {
      this.maxAge = maxAge;
   }

   public int getSMaxAge()
   {
      return sMaxAge;
   }

   public void setSMaxAge(int sMaxAge)
   {
      this.sMaxAge = sMaxAge;
   }

   public boolean isMustRevalidate()
   {
      return isMustRevalidate;
   }

   public void setMustRevalidate(boolean mustRevalidate)
   {
      isMustRevalidate = mustRevalidate;
   }

   public boolean isNoCache()
   {
      return isNoCache;
   }

   public void setNoCache(boolean noCache)
   {
      isNoCache = noCache;
   }

   public boolean isNoStore()
   {
      return isNoStore;
   }

   public void setNoStore(boolean noStore)
   {
      isNoStore = noStore;
   }

   public boolean isNoTransform()
   {
      return isNoTransform;
   }

   public void setNoTransform(boolean noTransform)
   {
      isNoTransform = noTransform;
   }

   public boolean isPrivate()
   {
      return isPrivate;
   }

   public void setPrivate(boolean aPrivate)
   {
      isPrivate = aPrivate;
   }

   public boolean isPublic()
   {
      return isPublic;
   }

   public void setPublic(boolean aPublic)
   {
      isPublic = aPublic;
   }

   public boolean isProxyRevalidate()
   {
      return isProxyRevalidate;
   }

   public void setProxyRevalidate(boolean proxyRevalidate)
   {
      isProxyRevalidate = proxyRevalidate;
   }


   public boolean isImmutable()
   {
      return isImmutable;
   }

   public void setImmutable(boolean value)
   {
      isImmutable = value;
   }


   public boolean isStaleIfError()
   {
      return isStaleIfError;
   }

   public void setStaleIfError(boolean value)
   {
      isStaleIfError = value;
   }


   public boolean isStaleWhileRevalidate()
   {
      return isStaleWhileRevalidate;
   }

   public void setStaleWhileRevalidate(boolean value)
   {
      isStaleWhileRevalidate = value;
   }



   /**
    * Returns <tt>max-age</tt>, or <tt>s-maxage</tt> according to whether the
    * cache is a shared cache, or a private cache. If shared cache and the
    * <tt>s-maxage</tt> is negative (i.e. not set), then returns
    * <tt>max-age</tt> instead.
    *
    * @param type the cache type, used to determine the correct age parameter
    * @return A {@link #maxAge}, or {@link #sMaxAge} according to the given
    * cache type argument.
    */
   public int getMaxAge(CacheType type)
   {
      if(isNoCache) return 0;
      return (type == Shared && sMaxAge >= 0) ? sMaxAge : maxAge;
   }

   public boolean getMustRevalidate(CacheType type)
   {
      if(isNoCache) return true;
      if(type == Shared) {
         return isProxyRevalidate || isMustRevalidate;
      }
      return isMustRevalidate;
   }

   public Visibility getVisibility()
   {
      if(isPublic) {
         return Visibility.Public;
      } else if(isPrivate) {
         return Visibility.Private;
      }
      return Visibility.Unknown;
   }

}
