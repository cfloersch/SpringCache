/*
 * Copyright 2019 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 10/8/2019
 */
package com.xpertss.cache;

import com.xpertss.cache.store.CacheItem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import xpertss.lang.Objects;

import java.io.IOException;
import java.io.InputStream;

public class CachedHttpResponse implements ClientHttpResponse {

   private final CacheItem cachedItem;
   private final HttpHeaders headers;

   public CachedHttpResponse(CacheItem cachedItem)
   {
      this.cachedItem = Objects.notNull(cachedItem, "cachedItem");
      this.headers = cachedItem.getHeaders();
   }


   @Override
   public HttpStatus getStatusCode()
      throws IOException
   {
      return cachedItem.getStatus();
   }

   @Override
   public int getRawStatusCode()
      throws IOException
   {
      return getStatusCode().value();
   }

   @Override
   public String getStatusText()
      throws IOException
   {
      return getStatusCode().getReasonPhrase();
   }

   @Override
   public void close()
   {
      // TODO Anything I need to do here
   }

   @Override
   public InputStream getBody()
      throws IOException
   {
      return cachedItem.newInput();
   }

   @Override
   public HttpHeaders getHeaders()
   {
      return headers;
   }

}
