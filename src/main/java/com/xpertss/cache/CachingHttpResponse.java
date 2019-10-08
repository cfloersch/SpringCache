/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package com.xpertss.cache;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import xpertss.lang.Objects;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class proxies a ClientHttpResponse and wraps the InputStream
 * so that the entity is written to the cache as it is read from the
 * network.
 */
public class CachingHttpResponse implements ClientHttpResponse {

   private final ClientHttpResponse proxied;
   private final OutputStream cache;

   public CachingHttpResponse(ClientHttpResponse proxied, OutputStream cache)
   {
      this.proxied = Objects.notNull(proxied, "proxied");
      this.cache = Objects.notNull(cache, "cache");
   }



   @Override
   public HttpStatus getStatusCode()
      throws IOException
   {
      return proxied.getStatusCode();
   }

   @Override
   public int getRawStatusCode()
      throws IOException
   {
      return proxied.getRawStatusCode();
   }

   @Override
   public String getStatusText()
      throws IOException
   {
      return proxied.getStatusText();
   }


   @Override
   public HttpHeaders getHeaders()
   {
      return proxied.getHeaders();
   }



   // TODO Need to create InputStream that wraps the proxied input stream and
   // writes everything to outputstream as bytes are read in.

   @Override
   public InputStream getBody()
      throws IOException
   {
      return null;
   }

   // TODO Do I need to close the proxied inputstream and thus the underlying
   //  inputstream and cache outputstream.
   @Override
   public void close()
   {

   }

}
