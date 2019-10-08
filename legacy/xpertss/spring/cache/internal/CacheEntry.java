package xpertss.spring.cache.internal;

import org.springframework.http.client.ClientHttpResponse;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class CacheEntry implements Serializable {

   private static final long serialVersionUID = 1L;

   private final InMemoryClientHttpResponse response;
   private final Date responseCreated;
   private final Date responseExpiration;

   /**
    * @param response           The response to cache.
    * @param responseCreated    When the response was originally created.
    * @param responseExpiration When the response will expire.
    */
   public CacheEntry(InMemoryClientHttpResponse response, Date responseCreated, Date responseExpiration)
   {
      this.response = response;
      this.responseCreated = responseCreated;
      this.responseExpiration = responseExpiration;
   }


   public ClientHttpResponse getResponse()
   {
      return response.deepCopy();
   }

   public Date getResponseCreated()
   {
      return new Date(responseCreated.getTime());
   }

   public Date getResponseExpiration()
   {
      return new Date(responseExpiration.getTime());
   }


   @Override
   public boolean equals(Object o)
   {
      if(o instanceof CacheEntry) {
         CacheEntry that = (CacheEntry) o;
         return Objects.equals(response, that.response) && Objects.equals(responseCreated, that.responseCreated) && Objects.equals(responseExpiration, that.responseExpiration);
      }
      return false;
   }

   @Override
   public int hashCode()
   {

      return Objects.hash(response, responseCreated, responseExpiration);
   }
}
