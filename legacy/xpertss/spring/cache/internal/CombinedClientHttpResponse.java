package xpertss.spring.cache.internal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ClientHttpResponse} implementation that combines an existing response
 * with a different body. It delegates all methods to the underlying
 * {@code ClientHttpResponse} expect {@link #getBody()}, that returns the
 * specified body instead of the one inside the underlying response.
 */
public class CombinedClientHttpResponse implements ClientHttpResponse {

   private final ClientHttpResponse response;

   private final InputStream body;


   /**
    * @param response The original response to decorate.
    * @param body     The body of the message as an input stream.
    */
   public CombinedClientHttpResponse(ClientHttpResponse response, InputStream body)
   {
      Assert.notNull(response, "response must not be null");
      this.response = response;
      this.body = body;
   }


   public InputStream getBody()
      throws IOException
   {
      return body;
   }

   public HttpHeaders getHeaders()
   {
      return response.getHeaders();
   }


   @Override
   public HttpStatus getStatusCode()
      throws IOException
   {
      return response.getStatusCode();
   }

   @Override
   public int getRawStatusCode()
      throws IOException
   {
      return response.getRawStatusCode();
   }

   @Override
   public String getStatusText()
      throws IOException
   {
      return response.getStatusText();
   }

   @Override
   public void close()
   {
      response.close();
   }
}
