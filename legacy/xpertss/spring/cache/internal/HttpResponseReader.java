package xpertss.spring.cache.internal;

import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public interface HttpResponseReader {

   /**
    * Reads the original {@link ClientHttpResponse} to memory, if possible,
    * and returns a serializable copy.
    *
    * @param response The original response to read.
    * @return An in-memory copy of the original response.
    * @throws IOException
    */
   InMemoryClientHttpResponse readResponse(ClientHttpResponse response)
      throws IOException;
}
