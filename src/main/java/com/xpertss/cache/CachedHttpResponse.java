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
import java.nio.MappedByteBuffer;

public class CachedHttpResponse implements ClientHttpResponse {

   private HttpStatus status;
   private HttpHeaders headers;
   private MappedByteBuffer data;

   public CachedHttpResponse(HttpStatus status, HttpHeaders headers, MappedByteBuffer data)
   {
      this.status = Objects.notNull(status, "status");
      this.headers = Objects.notNull(headers, "headers");
      this.data = Objects.notNull(data, "data");
   }


   @Override
   public HttpStatus getStatusCode()
      throws IOException
   {
      return status;
   }

   @Override
   public int getRawStatusCode()
      throws IOException
   {
      return status.value();
   }

   @Override
   public String getStatusText()
      throws IOException
   {
      return status.getReasonPhrase();
   }

   @Override
   public void close()
   {
      data.force();
   }

   @Override
   public InputStream getBody()
      throws IOException
   {
      return null;
   }

   @Override
   public HttpHeaders getHeaders()
   {
      return headers;
   }

}
