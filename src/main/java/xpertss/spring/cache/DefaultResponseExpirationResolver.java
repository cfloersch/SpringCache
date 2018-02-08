package xpertss.spring.cache;

import xpertss.spring.cache.internal.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;

import java.util.Date;

import static java.lang.Math.max;

public class DefaultResponseExpirationResolver implements ResponseExpirationResolver {

   /**
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.6">HTTP/1.1 section 14.6</a>
    */
   public static final long MAX_AGE = 2147483648L;

   private final boolean sharedCache;


   public DefaultResponseExpirationResolver()
   {
      this(true);
   }

   public DefaultResponseExpirationResolver(boolean sharedCache)
   {
      this.sharedCache = sharedCache;
   }


   public Date resolveExpirationDate(ClientHttpResponse response, Date correctedInitialDate)
   {
      long maxAge = parseMaxAgeHeader(response.getHeaders()) * 1000L; //ms

      return new Date(correctedInitialDate.getTime() + maxAge);
   }

   public Date resolveInitialDate(ClientHttpResponse response, Date requestDate, Date responseDate)
   {
      long age = correctedInitialAge(response, requestDate, responseDate) * 1000L; //ms

      return new Date(responseDate.getTime() + age);
   }

   /**
    * Calculates a corrected initial age of the request, i.e. number of seconds
    * the response was created before we received it.
    *
    * @param response     The response to compute initial age of.
    * @param requestDate  When the request was send.
    * @param responseDate When the response was received.
    * @return A corrected initial age in seconds.
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.2.3">HTTP/1.1 section 13</a>
    */
   public long correctedInitialAge(ClientHttpResponse response, Date requestDate, Date responseDate)
   {
      long ageValue = parseAgeHeader(response.getHeaders());
      long headerDate = response.getHeaders().getDate();

      long apparentAge = max(0, responseDate.getTime() - headerDate) / 1000L; //seconds
      long correctedReceivedAge = max(apparentAge, ageValue);
      long responseDelay = (responseDate.getTime() - requestDate.getTime()) / 1000L; //seconds

      return correctedReceivedAge + responseDelay;
   }

   /**
    * Parses value of the <tt>Age</tt> header from the given headers. When
    * the header is not specified, returns 0. When the header is specified
    * multiple times, then returns greater value. If the value is less then
    * zero or malformed, then {@link #MAX_AGE} is returned.
    *
    * @param headers HTTP headers of the response.
    * @return An <tt>Age</tt> header value.
    */
   long parseAgeHeader(HttpHeaders headers)
   {
      long result = 0;

      if(!headers.containsKey("Age")) {
         return result;
      }
      for(String value : headers.get("Age")) {
         long age = parseLong(value, MAX_AGE);
         if(age < 0) age = MAX_AGE;

         result = max(result, age);
      }
      return result;
   }

   private int parseMaxAgeHeader(HttpHeaders headers)
   {
      CacheControl cc = CacheControl.valueOf(headers.getCacheControl());
      return cc.getMaxAge(sharedCache);
   }

   private long parseLong(String str, long def)
   {
      try {
         return Long.parseLong(str);
      } catch(Exception e) {
         return def;
      }
   }
}
