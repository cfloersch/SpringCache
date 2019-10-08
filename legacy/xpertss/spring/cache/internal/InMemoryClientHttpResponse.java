package xpertss.spring.cache.internal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

public class InMemoryClientHttpResponse implements ClientHttpResponse, Serializable {

   private static final long serialVersionUID = 1L;

   private final byte[] body;
   private final HttpStatus statusCode;
   private final HttpHeaders headers;


   public InMemoryClientHttpResponse(byte[] body, HttpStatus statusCode, HttpHeaders headers)
   {
      Assert.notNull(statusCode, "statusCode must not be null");
      this.body = body != null ? body : new byte[0];
      this.statusCode = statusCode;
      this.headers = headers != null ? headers : new HttpHeaders();
   }

   public InputStream getBody()
   {
      return new ByteArrayInputStream(body);
   }

   public byte[] getBodyAsByteArray()
   {
      return body;
   }

   @Override
   public HttpStatus getStatusCode()
      throws IOException
   {
      return statusCode;
   }

   @Override
   public HttpHeaders getHeaders()
   {
      return headers;
   }

   public int getRawStatusCode()
   {
      return statusCode.value();
   }

   public String getStatusText()
   {
      return statusCode.getReasonPhrase();
   }

   public void close()
   {
      // do nothing
   }

   public InMemoryClientHttpResponse deepCopy()
   {
      HttpHeaders headersCopy = new HttpHeaders();
      for(Entry<String, List<String>> entry : headers.entrySet()) {
         headersCopy.put(entry.getKey(), new LinkedList<>(entry.getValue()));
      }
      return new InMemoryClientHttpResponse(body.clone(), statusCode, headersCopy);
   }

   @Override
   public boolean equals(Object o)
   {
      if(o instanceof InMemoryClientHttpResponse) {
         InMemoryClientHttpResponse that = (InMemoryClientHttpResponse) o;
         return Arrays.equals(body, that.body) &&
                  statusCode == that.statusCode &&
                  Objects.equals(headers, that.headers);
      }
      return false;
   }

   @Override
   public int hashCode()
   {
      int result = Objects.hash(statusCode, headers);
      result = 31 * result + Arrays.hashCode(body);
      return result;
   }



}
